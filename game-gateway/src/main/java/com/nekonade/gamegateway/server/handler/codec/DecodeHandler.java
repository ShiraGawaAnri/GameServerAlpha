package com.nekonade.gamegateway.server.handler.codec;

import com.nekonade.gamegateway.server.handler.codec.GateRequestMessage;
import com.nekonade.network.message.game.GameMessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.Setter;

public class DecodeHandler extends ChannelInboundHandlerAdapter {

    @Setter
    private String aesSecret;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            try {
                GameMessageHeader header = new GameMessageHeader();
                header.readRequestHeader(byteBuf);
                GateRequestMessage gameMessagePackage = new GateRequestMessage(header,byteBuf,aesSecret);
                ctx.fireChannelRead(gameMessagePackage);
            }finally {
                ReferenceCountUtil.safeRelease(byteBuf);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
