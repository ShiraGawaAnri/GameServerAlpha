package com.nekonade.gamegateway.server.handler;

import com.google.common.util.concurrent.RateLimiter;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessagePackage;
import com.nekonade.network.param.game.message.neko.EnterGameMsgRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

//TODO:实现登录的排队效果

public class RequestRateLimiterHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RequestRateLimiterHandler.class);
    private static RateLimiter userRateLimiter;// 用户限流器，用于限制单个用户的请求。
    //测试使用封装过的RateLimiter
    //private final RateLimiterController rateLimiterController;
    private final RateLimiter globalRateLimiter; // 全局限制器
    private int lastClientSeqId = 0;
    private final EnterGameRateLimiterController waitingLinesController;
    private final EnterGameMsgRequest enterGameMsgRequest;

    public RequestRateLimiterHandler(RateLimiter globalRateLimiter, EnterGameRateLimiterController waitingLinesController, double requestPerSecond) {
        this.globalRateLimiter = globalRateLimiter;
        this.waitingLinesController = waitingLinesController;
        this.userRateLimiter = RateLimiter.create(requestPerSecond);
        this.enterGameMsgRequest = new EnterGameMsgRequest();
    }

    private boolean enteredGame = false;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GameMessagePackage gameMessagePackage = (GameMessagePackage) msg;
        int messageId = gameMessagePackage.getHeader().getMessageId();
        Boolean isEnterGameRequest = messageId == enterGameMsgRequest.getMessageId();
//        EnumMessageType messageType = gameMessagePackage.getHeader().getMessageType();
//        Boolean isEnterGameRequest = enterGameMsgRequest.sameMessageMeta(messageId, messageType);
        long playerId = gameMessagePackage.getHeader().getPlayerId();
        if(isEnterGameRequest && !enteredGame){
            if(!waitingLinesController.acquire(playerId)){
                logger.debug("channel {} 的playerId {} 正在排队中", ctx.channel().id().asShortText(),playerId);
                //ctx.close();
                return;
            }
            this.enteredGame = true;
        }else {
            if(!this.enteredGame){
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
        if (!globalRateLimiter.tryAcquire(1,2, TimeUnit.SECONDS)) {// 获取全局令牌失败，触发限流
            logger.debug("全局请求超载，channel {} 断开", ctx.channel().id().asShortText());
            ctx.close();
            return;
        }
        globalRateLimiter.acquire();
        int clientSeqId = gameMessagePackage.getHeader().getClientSeqId();
        if (lastClientSeqId > 0) {
            if (clientSeqId <= lastClientSeqId) {
                return;//直接返回，不再处理。
            }
        }
        this.lastClientSeqId = clientSeqId;
        ctx.fireChannelRead(msg);//不要忘记添加这个，要不然后面的handler收不到消息
    }
}
