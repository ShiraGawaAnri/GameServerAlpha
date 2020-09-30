package com.nekonade.gamegateway.server.handler.codec;

import com.nekonade.common.utils.CompressUtil;
import com.nekonade.gamegateway.common.GatewayServerConfig;
import com.nekonade.network.message.game.GameMessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncodeHandler extends MessageToByteEncoder<GateResponseMessage> {

    private final GatewayServerConfig serverConfig;

    private static final Logger logger = LoggerFactory.getLogger(EncodeHandler.class);

    public EncodeHandler(GatewayServerConfig serverConfig) {
        this.serverConfig = serverConfig;// 注入服务端配置
    }

    @Setter
    private String aesSecret;// 对称加密密钥

    @Override
    protected void encode(ChannelHandlerContext ctx, GateResponseMessage msg, ByteBuf out) throws Exception {
        try{
            int messageSize = GameMessageHeader.HEADER_LENGTH;
            byte[] body = msg.getResponseBody();
            int compress = 0;
            if(body != null){
                if(body.length > serverConfig.getCompressMessageSize()){
                    body = CompressUtil.compress(body);
                    compress = 1;
                }
//                if(this.aesSecret != null && msg.getHeader().getMessageId() != 1){
//
//                }
                messageSize += 1;//添加压缩标记所点字节长度
                messageSize += body.length;
            }
            messageSize += 4;//记录消息总长度所占字节长度
            out.writeInt(messageSize);
            GameMessageHeader header = msg.getHeader();
            header.writeResponseHeader(out);
            if (body != null) {
                out.writeByte(compress);
                out.writeBytes(body);
            }
        }catch (Exception e){
            logger.error("网关编码消息失败",e);
        }
    }
}
