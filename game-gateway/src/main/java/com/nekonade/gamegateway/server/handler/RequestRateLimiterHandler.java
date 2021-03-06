package com.nekonade.gamegateway.server.handler;

import com.google.common.util.concurrent.RateLimiter;
import com.nekonade.common.error.*;
import com.nekonade.common.utils.GameTimeUtils;
import com.nekonade.gamegateway.common.RequestConfigLimiters;
import com.nekonade.gamegateway.common.RequestConfigs;
import com.nekonade.common.gameMessage.GameMessagePackage;
import com.nekonade.network.param.game.message.neko.error.GameNotificationMsgResponse;
import com.nekonade.network.param.message.GatewayMessageCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RequestRateLimiterHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RequestRateLimiterHandler.class);
    private static RateLimiter userRateLimiter;// 用户限流器，用于限制单个用户的请求。
    //测试使用封装过的RateLimiter
    //private final RateLimiterController rateLimiterController;
    private final RateLimiter globalRateLimiter; // 全局限制器
    private int lastClientSeqId = 0;
    private final EnterGameRateLimiterController waitingLinesController;
    private final RequestConfigs requestConfigs;

    public RequestRateLimiterHandler(RateLimiter globalRateLimiter, EnterGameRateLimiterController waitingLinesController, double requestPerSecond, RequestConfigs requestConfigs) {
        this.globalRateLimiter = globalRateLimiter;
        this.waitingLinesController = waitingLinesController;
        userRateLimiter = RateLimiter.create(requestPerSecond);
        this.requestConfigs = requestConfigs;
    }

    private boolean enteredGame = false;

    private GameNotificationMsgResponse buildResponse(BasicException error){
        ErrorResponseEntity errorEntity = new ErrorResponseEntity();
        errorEntity.setErrorCode(error.getError().getErrorCode());
        errorEntity.setErrorMsg(error.getError().getErrorDesc());
        errorEntity.setData(error.getData());
        GameNotificationMsgResponse response;
        response = new GameNotificationMsgResponse();
        response.getBodyObj().setError(errorEntity);
        return response;
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GameMessagePackage gameMessagePackage = (GameMessagePackage) msg;
        int messageId = gameMessagePackage.getHeader().getMessageId();
        boolean isEnterGameRequest = messageId == GatewayMessageCode.EnterGame.getMessageId();
        boolean isHeartBeatRequest = messageId == GatewayMessageCode.Heartbeat.getMessageId();
        long playerId = gameMessagePackage.getHeader().getPlayerId();

        //检查请求是否被配置文件拒绝

        if(requestConfigs != null){
            long startTime = 0;
            long endTime = 0;
            boolean allServerMaintenance = requestConfigs.isAllServerMaintenance();
            boolean triggered = false;
            boolean maintenanceNotify = false;
            if(allServerMaintenance){
                startTime = requestConfigs.getMaintenanceStartTime();
                endTime = requestConfigs.getMaintenanceEndTime();
                maintenanceNotify = true;
                triggered = GameTimeUtils.checkTimeIsBetween(startTime,endTime);
            }else{
                List<RequestConfigLimiters> limiters = requestConfigs.getLimiters();
                if(limiters != null && limiters.size() > 0){
                    Optional<RequestConfigLimiters> op = limiters.stream().filter(requestLimiter -> requestLimiter.getMessageId() == messageId).findAny();
                    if(op.isPresent()){
                        RequestConfigLimiters requestLimiter = op.get();
                        logger.info("模拟拒绝已拦截请求:{}", requestLimiter);
                        startTime = requestLimiter.getStartTime();
                        endTime = requestLimiter.getEndTime();
                        maintenanceNotify = requestLimiter.isMaintenance();
                        boolean switchOn = requestLimiter.isSwitchOn();
                        if(switchOn){
                            triggered = GameTimeUtils.checkTimeIsBetween(startTime,endTime);
                        }
                    }
                }
            }
            if(triggered){
                GameNotifyException error;
                if(maintenanceNotify){
                    Map<String, Object> map = new HashMap<>();
                    if(startTime != 0){
                        map.put("startTime",startTime);
                    }
                    if(endTime != 0){
                        map.put("endTime",endTime);
                    }
                    error = GameNotifyException.newBuilder(GatewayMessageCode.RequestFunctionMaintenance).data(map).build();
                }else{
                    error = GameNotifyException.newBuilder(GatewayMessageCode.RequestRefuse).build();
                }
                GameNotificationMsgResponse response = buildResponse(error);
                GameMessagePackage returnPackage = new GameMessagePackage();
                returnPackage.setHeader(response.getHeader());
                returnPackage.setBody(response.body());
                ctx.writeAndFlush(returnPackage);
                return;
            }

        }
        if(isEnterGameRequest && !enteredGame){
            if(!waitingLinesController.acquire(playerId)){
                logger.debug("channel {} 的playerId {} 正在排队中", ctx.channel().id().asShortText(),playerId);
                //ctx.close();
                Map<String, Double> map = new HashMap<>();
                map.put("lines", (double) waitingLinesController.getLineLength());
                map.put("time",waitingLinesController.getRestTime());
                GameNotifyException error = GameNotifyException.newBuilder(GatewayMessageCode.WaitLines).data(map).build();
                GameNotificationMsgResponse response = buildResponse(error);
                GameMessagePackage returnPackage = new GameMessagePackage();
                returnPackage.setHeader(response.getHeader());
                returnPackage.setBody(response.body());
                ctx.writeAndFlush(returnPackage);
                return;
            }
            this.enteredGame = true;
        }else {
            if(!this.enteredGame && !isHeartBeatRequest){
                logger.debug("channel {} 的playerId {} 未经排队但直接进行其他请求,已驳回", ctx.channel().id().asShortText(),playerId);
                return;
            }
            if (!userRateLimiter.tryAcquire(1,1, TimeUnit.SECONDS)) {// 获取令牌失败，触发限流
                logger.debug("channel {} 请求过多，连接断开", ctx.channel().id().asShortText());
                ctx.close();
                return;
            }
        }
        userRateLimiter.acquire();

        if(!isHeartBeatRequest){
            if (!globalRateLimiter.tryAcquire(1,2, TimeUnit.SECONDS)) {// 获取全局令牌失败，触发限流
                logger.debug("全局请求超载，channel {} 断开", ctx.channel().id().asShortText());
                ctx.close();
                return;
            }
            globalRateLimiter.acquire();
        }
        int clientSeqId = gameMessagePackage.getHeader().getClientSeqId();
        if (lastClientSeqId > 0) {
            if (clientSeqId <= lastClientSeqId) {
                logger.warn("延迟消息 ClientSeqId {} {} ",clientSeqId,gameMessagePackage);
                return;//直接返回，不再处理。
            }
        }
        this.lastClientSeqId = clientSeqId;
        ctx.fireChannelRead(msg);//不要忘记添加这个，要不然后面的handler收不到消息
    }
}
