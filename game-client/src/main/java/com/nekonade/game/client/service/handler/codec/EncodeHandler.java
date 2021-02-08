package com.nekonade.game.client.service.handler.codec;

import com.nekonade.common.utils.AESUtils;
import com.nekonade.common.utils.CompressUtil;
import com.nekonade.game.client.service.GameClientConfig;
import com.nekonade.network.param.game.common.GameMessageHeader;
import com.nekonade.network.param.game.common.IGameMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;

/**
 * @ClassName: EncodeHandler
 * @Description: 客户端编码类
 * @author: wgs
 * @date: 2019年4月1日 上午11:13:39
 */
public class EncodeHandler extends MessageToByteEncoder<IGameMessage> {
    /**
     * 发送消息的包头总长度：即：消息总长度(4) + 客户端消息序列号长度(4) + 消息请求ID长度（4） + 服务ID(2) + 客户端发送时间长度(8) + 协议版本长度(4) +
     * 是否压缩长度(1)
     */
    private static final int GAME_MESSAGE_HEADER_LEN = 27;
    private final GameClientConfig gameClientConfig;
    @Setter
    private String aesScreteKey;//对称加密的密钥
    private int seqId;//消息序列号

    public EncodeHandler(GameClientConfig gameClientConfig) {
        this.gameClientConfig = gameClientConfig;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, IGameMessage msg, ByteBuf out) throws Exception {
        int messageSize = GAME_MESSAGE_HEADER_LEN;// 标记数据包的总大小
        byte[] body = msg.body();
        int compress = 0;//标记包体是否进行了压缩
        if (body != null) {
            if (body.length >= gameClientConfig.getMessageCompressSize()) { // 从配置中获取达到压缩的包体的最小大小。
                body = CompressUtil.compress(body);//包体大小达到压缩的最上值时，对包体进行压缩
                compress = 1;
            }
            if (this.aesScreteKey != null && msg.getHeader().getMessageId() != 1) {
                //密钥不为空，对消息体加密
                body = AESUtils.encode(aesScreteKey, body);
            }
            messageSize += body.length;//加上包体的长度，得到数据包的总大小。
        }
        GameMessageHeader header = msg.getHeader();
        out.writeInt(messageSize);// 依次写入包头数据。
        out.writeInt(++seqId);
        out.writeInt(header.getMessageId());
        out.writeShort(header.getServiceId());
        out.writeLong(header.getClientSendTime());
        out.writeInt(gameClientConfig.getVersion());// 从配置中获取客户端版本
        out.writeByte(compress);
        if (body != null) {//如果包体不为空，写入包体数据
            out.writeBytes(body);
        }
    }

}
