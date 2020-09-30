package com.nekonade.network.message.game;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class GameMessageHeader implements Cloneable {
    public static int HEADER_LENGTH = 25;
    public static int HEADER_INNER_LENGTH = 53;
    private int messageSize;
    private int messageId;
    private int serviceId;
    private long clientSendTime;
    private long serverSendTime;
    private int clientSeqId;
    private int version;
    private int errorCode;
    private int fromServerId;
    private int toServerId;
    private long playerId;
    private boolean compress;
    private EnumMesasageType mesasageType;
    
    private String desc;

    private HeaderAttribute attribute = new HeaderAttribute();

    @Override
    public GameMessageHeader clone() throws CloneNotSupportedException {
        GameMessageHeader newHeader = (GameMessageHeader) super.clone();
        return newHeader;
    }

    public void readRequestHeader(ByteBuf byteBuf) {
        messageSize = byteBuf.readInt();
        clientSeqId = byteBuf.readInt();
        messageId = byteBuf.readInt();
        serviceId = byteBuf.readInt();
        int type = byteBuf.readByte();
        mesasageType = EnumMesasageType.getType(type);
        clientSendTime = byteBuf.readLong();
        version = byteBuf.readInt();
        if (byteBuf.readableBytes() > 0) {
            int compress = byteBuf.readByte();
            this.compress = compress == 1;
        }
    }

    public void writeResponseHeader(ByteBuf out) {
        out.writeByte(this.getMesasageType().getType());
        out.writeInt(this.getClientSeqId());
        out.writeInt(this.getMessageId());
        out.writeLong(this.getServerSendTime());
        out.writeInt(this.getVersion());
        out.writeInt(this.getErrorCode());
    }
    
    public void readInnerHeader(ByteBuf byteBuf) throws UnsupportedEncodingException {
    	 messageSize = byteBuf.readInt();//依次读取包头信息
         toServerId = byteBuf.readInt();
         fromServerId = byteBuf.readInt();
         clientSeqId = byteBuf.readInt();
         messageId = byteBuf.readInt();
         serviceId = byteBuf.readInt();
         int  type = byteBuf.readByte();
         mesasageType = EnumMesasageType.getType(type);
         version = byteBuf.readInt();
         clientSendTime = byteBuf.readLong();
         serverSendTime = byteBuf.readLong();
         playerId = byteBuf.readLong();
         errorCode = byteBuf.readInt();
         int attLeng = byteBuf.readInt();
         byte[] attrBytes = new byte[attLeng];
         byteBuf.readBytes(attrBytes);
         String attrJson = new String(attrBytes, StandardCharsets.UTF_8);
         this.attribute = JSON.parseObject(attrJson,HeaderAttribute.class);
        
    }
    
    public void writeInnerHeader(ByteBuf byteBuf)  {
    	byteBuf.writeInt(this.getToServerId());
        byteBuf.writeInt(this.getFromServerId());
        byteBuf.writeInt(this.getClientSeqId());
        byteBuf.writeInt(this.getMessageId());
        byteBuf.writeInt(this.getServiceId());
        byteBuf.writeByte(this.getMesasageType().getType());
        byteBuf.writeInt(this.getVersion());
        byteBuf.writeLong(this.getClientSendTime());
        byteBuf.writeLong(this.getServerSendTime());
        byteBuf.writeLong(this.getPlayerId());
        byteBuf.writeInt(this.getErrorCode());
    }
    
    public byte[] getHeaderAttributeBytes() throws UnsupportedEncodingException {
    	String attrJson = JSON.toJSONString(attribute);
        byte[] attrBytes = attrJson.getBytes(StandardCharsets.UTF_8);
        return attrBytes;
    }

    @Override
    public String toString() {
        return desc + " [" + this.clientSeqId + "]" + " : GameMessageHeader [messageSize=" + messageSize + ", messageId=" + messageId + ", serviceId=" + serviceId + ", clientSendTime=" + clientSendTime + ", serverSendTime=" + serverSendTime + ", clientSeqId=" + clientSeqId + ", version=" + version + ", errorCode=" + errorCode + ", fromServerId=" + fromServerId + ", toServerId=" + toServerId + ", playerId=" + playerId + ", attribute=" + attribute + "]";
    }

}
