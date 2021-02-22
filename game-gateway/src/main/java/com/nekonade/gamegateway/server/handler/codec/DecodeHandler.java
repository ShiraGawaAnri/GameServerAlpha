package com.nekonade.gamegateway.server.handler.codec;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nekonade.common.utils.AESUtils;
import com.nekonade.common.utils.CompressUtils;
import com.nekonade.network.param.game.common.GameMessageHeader;
import com.nekonade.network.param.game.common.GameMessagePackage;
import com.nekonade.network.param.game.common.HeaderAttribute;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;

public class DecodeHandler extends ChannelInboundHandlerAdapter {

    private final ApplicationContext context;

    private final ObjectMapper objectMapper;

    @Setter
    private String aesSecret;//对称加密密钥

    public DecodeHandler(ApplicationContext applicationContext) {
        this.context = applicationContext;
        this.objectMapper = this.context.getBean(ObjectMapper.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        try {
            int messageSize = byteBuf.readInt();
            int clientSeqId = byteBuf.readInt();
            int messageId = byteBuf.readInt();
            int serviceId = byteBuf.readShort();
            long clientSendTime = byteBuf.readLong();
            int version = byteBuf.readInt();
            int headerAttrLength = byteBuf.readInt();
            HeaderAttribute headerAttr = null;
            if (headerAttrLength > 0) {//读取包头属性
                byte[] headerAttrBytes = new byte[headerAttrLength];
                byteBuf.readBytes(headerAttrBytes);
                String headerAttrJson = new String(headerAttrBytes);
                //headerAttr = JSON.parseObject(headerAttrJson, HeaderAttribute.class);
                headerAttr = objectMapper.readValue(headerAttrJson,HeaderAttribute.class);
            }
            int compress = byteBuf.readByte();
            byte[] body = null;
            if (byteBuf.readableBytes() > 0) {
                body = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(body);
                if (this.aesSecret != null && messageId != 1) {//如果密钥不为空，且不是认证消息，对消息体解密
                    body = AESUtils.decode(aesSecret, body);
                }
                if (compress == 1) {
                    body = CompressUtils.decompress(body);
                }
            }
            GameMessageHeader header = new GameMessageHeader();
            header.setAttribute(headerAttr);
            header.setClientSendTime(clientSendTime);
            header.setClientSeqId(clientSeqId);
            header.setMessageId(messageId);
            header.setServiceId(serviceId);
            header.setMessageSize(messageSize);
            header.setVersion(version);
            GameMessagePackage gameMessagePackage = new GameMessagePackage();
            gameMessagePackage.setHeader(header);
            gameMessagePackage.setBody(body);
            ctx.fireChannelRead(gameMessagePackage);
        } finally {
            ReferenceCountUtil.release(byteBuf);
        }
    }
}
