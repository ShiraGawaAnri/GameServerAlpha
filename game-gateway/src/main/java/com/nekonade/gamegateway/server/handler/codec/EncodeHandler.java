package com.nekonade.gamegateway.server.handler.codec;

import com.nekonade.common.utils.AESUtils;
import com.nekonade.common.utils.CompressUtils;
import com.nekonade.common.utils.MessageUtils;
import com.nekonade.gamegateway.common.GatewayServerConfig;
import com.nekonade.common.gameMessage.GameMessageHeader;
import com.nekonade.common.gameMessage.GameMessagePackage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class EncodeHandler extends MessageToByteEncoder<GameMessagePackage> {

    private final static Logger logger = LoggerFactory.getLogger(EncodeHandler.class);

    private static final int GAME_MESSAGE_HEADER_LEN = 37;
    private final GatewayServerConfig serverConfig;
    @Setter
    private String aesSecret;// 对称加密密钥

    public EncodeHandler(GatewayServerConfig serverConfig, ApplicationContext applicationContext) {
        this.serverConfig = serverConfig;// 注入服务端配置
    }

    /**
     * 发送消息的包头总长度：即：
     * 消息总长度(4) +
     * 消息类型(1) +
     * 客户端消息序列号长度(4) +
     * 消息请求ID长度（4） +
     * 服务端发送时间长度(8) +
     * 协议版本长度(4) +
     * 错误码(4) +
     * 玩家ID(8) + //多线程测试用
     * 是否压缩长度(1)
     */


    @Override
    protected void encode(ChannelHandlerContext ctx, GameMessagePackage msg, ByteBuf out) throws Exception {
        int messageSize = GAME_MESSAGE_HEADER_LEN;
        byte[] body = msg.getBody();
        int compress = 0;
        if (body != null) {// 达到压缩条件，进行压缩

            if (body.length >= serverConfig.getCompressMessageSize()) {
                body = CompressUtils.compress(body);
                compress = 1;
            }
            if (this.aesSecret != null && msg.getHeader().getMessageId() != 1) {
                //logger.info("messageId {} Body加密前 {}",msg.getHeader().getMessageId(),new String(body));
                body = AESUtils.encode(aesSecret, body);
            }
            messageSize += body.length;
        }
        out.writeInt(messageSize);
        GameMessageHeader header = msg.getHeader();
        out.writeInt(header.getClientSeqId());
        out.writeInt(header.getMessageId());
        out.writeLong(header.getServerSendTime());
        out.writeInt(header.getVersion());
        out.writeInt(header.getErrorCode());
        out.writeLong(header.getPlayerId());
        out.writeByte(compress);
        if (body != null) {
            out.writeBytes(body);
        }
        MessageUtils.CalcMessageDealTime(logger,msg);
        //ReferenceCountUtil.release(out);
    }
}
