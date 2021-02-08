package com.nekonade.gamegateway.server.handler.codec;

import com.nekonade.common.utils.AESUtils;
import com.nekonade.common.utils.CompressUtil;
import com.nekonade.gamegateway.common.GatewayServerConfig;
import com.nekonade.network.param.game.common.GameMessageHeader;
import com.nekonade.network.param.game.common.GameMessagePackage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;

public class EncodeHandler extends MessageToByteEncoder<GameMessagePackage> {

    private static final int GAME_MESSAGE_HEADER_LEN = 29;
    private final GatewayServerConfig serverConfig;
    @Setter
    private String aesSecret;// 对称加密密钥

    public EncodeHandler(GatewayServerConfig serverConfig) {
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
     * 是否压缩长度(1)
     */


    @Override
    protected void encode(ChannelHandlerContext ctx, GameMessagePackage msg, ByteBuf out) throws Exception {
        int messageSize = GAME_MESSAGE_HEADER_LEN;
        byte[] body = msg.getBody();
        int compress = 0;
        if (body != null) {// 达到压缩条件，进行压缩

            if (body.length >= serverConfig.getCompressMessageSize()) {
                body = CompressUtil.compress(body);
                compress = 1;
            }
            if (this.aesSecret != null && msg.getHeader().getMessageId() != 1) {
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
        out.writeByte(compress);
        out.writeInt(header.getErrorCode());
        if (body != null) {
            out.writeBytes(body);
        }
    }
}
