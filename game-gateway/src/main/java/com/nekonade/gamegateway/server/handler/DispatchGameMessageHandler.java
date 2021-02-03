package com.nekonade.gamegateway.server.handler;


import com.nekonade.common.cloud.PlayerServiceInstance;
import com.nekonade.common.error.GameErrorException;
import com.nekonade.common.error.GameGatewayError;
import com.nekonade.common.error.GameGatewayErrorEntity;
import com.nekonade.common.utils.JWTUtil;
import com.nekonade.common.utils.NettyUtils;
import com.nekonade.common.utils.TopicUtil;
import com.nekonade.gamegateway.common.GatewayServerConfig;
import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.network.param.game.common.GameMessagePackage;
import com.nekonade.network.param.game.message.neko.GameGatewayErrorMsgResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;

public class DispatchGameMessageHandler extends ChannelInboundHandlerAdapter {
    private final PlayerServiceInstance playerServiceInstance;// 注入业务服务管理类，从这里获取负载均衡的服务器信息
    private final GatewayServerConfig gatewayServerConfig; // 注入游戏网关服务配置信息。
    private JWTUtil.TokenBody tokenBody;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(DispatchGameMessageHandler.class);

    public DispatchGameMessageHandler(KafkaTemplate<String, byte[]> kafkaTemplate, PlayerServiceInstance playerServiceInstance, GatewayServerConfig gatewayServerConfig) {
        this.playerServiceInstance = playerServiceInstance;
        this.gatewayServerConfig = gatewayServerConfig;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        GameMessagePackage gameMessagePackage = (GameMessagePackage) msg;
        int serviceId = gameMessagePackage.getHeader().getServiceId();
        if (tokenBody == null) {// 如果首次通信，获取验证信息
            ConfirmHandler confirmHandler = (ConfirmHandler) ctx.channel().pipeline().get("ConfirmHandler");
            tokenBody = confirmHandler.getTokenBody();
        }
        String clientIp = NettyUtils.getRemoteIP(ctx.channel());
        dispatchMessage(kafkaTemplate, ctx, playerServiceInstance, tokenBody.getPlayerId(), serviceId, clientIp, gameMessagePackage, gatewayServerConfig);
    }
    
    public static void dispatchMessage(KafkaTemplate<String, byte[]> kafkaTemplate, ChannelHandlerContext ctx, PlayerServiceInstance playerServiceInstance, long playerId, int serviceId, String clientIp, GameMessagePackage gameMessagePackage, GatewayServerConfig gatewayServerConfig) {
        EventExecutor executor = ctx.executor();
        Promise<Integer> promise = new DefaultPromise<>(executor);
        playerServiceInstance.selectServerId(playerId, serviceId, promise).addListener((GenericFutureListener<Future<Integer>>) future -> {
            if (future.isSuccess()) {
                Integer toServerId = future.get();
                gameMessagePackage.getHeader().setToServerId(toServerId);
                gameMessagePackage.getHeader().setFromServerId(gatewayServerConfig.getServerId());
                gameMessagePackage.getHeader().getAttribute().setClientIp(clientIp);
                gameMessagePackage.getHeader().setPlayerId(playerId);
                String topic = TopicUtil.generateTopic(gatewayServerConfig.getBusinessGameMessageTopic(), toServerId);// 动态创建与业务服务交互的消息总线Topic
                byte[] value = GameMessageInnerDecoder.sendMessage(gameMessagePackage);// 向消息总线服务发布客户端请求消息。
                ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, String.valueOf(playerId), value);
                kafkaTemplate.send(record);
                logger.info("发送到{}\r\n消息成功",gameMessagePackage.getHeader());
            } else {
                Throwable error = future.cause();
                GameErrorException exception;
                if(error instanceof GameErrorException){
                    exception = (GameErrorException) error;
                }else{
                    exception = GameErrorException.newBuilder(GameGatewayError.GAME_GATEWAY_ERROR).build();
                }

                GameMessagePackage returnPackage = new GameMessagePackage();
                GameGatewayErrorMsgResponse response = new GameGatewayErrorMsgResponse();
                GameGatewayErrorEntity errorEntity = new GameGatewayErrorEntity();
                errorEntity.setErrorCode(exception.getError().getErrorCode());
                errorEntity.setErrorMsg(exception.getError().getErrorDesc());
                response.getBodyObj().setError(errorEntity);
//                response.getBodyObj().setErrorCode(exception.getError().getErrorCode());
//                response.getBodyObj().setErrorMessage(exception.getError().getErrorDesc());
                returnPackage.setHeader(response.getHeader());
                returnPackage.setBody(response.body());
                ctx.writeAndFlush(returnPackage);
                logger.error("消息发送失败", error);
            }
        });
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
         ctx.close();
         logger.error("服务器异常，连接{}断开",ctx.channel().id().asShortText(),cause);
    }
}
