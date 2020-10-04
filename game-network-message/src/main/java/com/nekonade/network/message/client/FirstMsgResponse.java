package com.mygame.game.message;

import com.mygame.game.common.AbstractGameMessage;
import com.xinyue.network.message.game.EnumMesasageType;
import com.xinyue.network.message.game.GameMessageMetadata;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@GameMessageMetadata(messageId = 10001, serviceId = 1, messageType = EnumMesasageType.RESPONSE) // 添加元数据信息
public class FirstMsgResponse extends AbstractGameMessage {
    private Long serverTime;//返回服务器的时间
    @Override
    public byte[] encode() {
        ByteBuf byteBuf = Unpooled.buffer(8);
        byteBuf.writeLong(serverTime);
        return byteBuf.array();
    }

    @Override
    protected void decode(byte[] body) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(body);
        this.serverTime = byteBuf.readLong();
    }

    @Override
    protected boolean isBodyMsgNull() {
        return this.serverTime == null;
    }

    public Long getServerTime() {
        return serverTime;
    }

    public void setServerTime(Long serverTime) {
        this.serverTime = serverTime;
    }

}
