package com.nekonade.gamegateway.server.handler;

import com.nekonade.gamegateway.messages.HeartbeatMsgRequest;
import com.nekonade.gamegateway.messages.HeartbeatMsgResponse;
import com.nekonade.gamegateway.messages.MessageCode;
import com.nekonade.gamegateway.server.handler.codec.GateRequestMessage;
import com.nekonade.gamegateway.server.handler.codec.GateResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);

    private int heartbeatCount = 0;// 心跳计数器，如果一直接收到的是心跳消息，达到一定数量之后，说明客户端一直没有用户操作了，服务器就主动断开连接。

    private final int maxHeartbeatCount = 150;// 最大心跳数

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {// 在这里接收channel中的事件信息
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {// 一定时间内，既没有收到客户端信息，则断开连接
                ctx.close();
                logger.debug("连接读取空闲，断开连接，channelId:{}", ctx.channel().id().asShortText());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GateRequestMessage gameMessagePackage = (GateRequestMessage) msg;// 拦截心跳请求，并处理
        if (gameMessagePackage.getHeader().getMessageId() == MessageCode.Heartbeat.getMessageId()) {
            logger.debug("收到心跳信息,[{}] channelid:{}",gameMessagePackage.getHeader().getClientSeqId(), ctx.channel().id().asShortText());
            HeartbeatMsgRequest request = new HeartbeatMsgRequest();
            gameMessagePackage.getGameMessage(request);
            HeartbeatMsgResponse response = new HeartbeatMsgResponse();
            GateResponseMessage returnPackage = new GateResponseMessage(request, response);
            ctx.writeAndFlush(returnPackage);
            this.heartbeatCount++;
            if (this.heartbeatCount > maxHeartbeatCount) {
                ctx.close();
            }
        } else {
            this.heartbeatCount = 0;// 收到非心跳消息之后，重新计数
            ctx.fireChannelRead(msg);
        }

    }
}
