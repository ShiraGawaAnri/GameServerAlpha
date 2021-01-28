package com.nekonade.network.param.game.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameMessageHeader implements Cloneable{
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
    private EnumMesasageType mesasageType;
    
    private HeaderAttribute attribute = new HeaderAttribute();
    
    @Override
    public GameMessageHeader clone() throws CloneNotSupportedException {
        GameMessageHeader newHeader = (GameMessageHeader) super.clone();
        
        return newHeader;
    }
    @Override
    public String toString() {
        return "GameMessageHeader [messageSize=" + messageSize + ", messageId=" + messageId + ", serviceId=" + serviceId + ", clientSendTime=" + clientSendTime + ", serverSendTime=" + serverSendTime + ", clientSeqId=" + clientSeqId + ", version=" + version + ", errorCode=" + errorCode + ", fromServerId=" + fromServerId + ", toServerId=" + toServerId + ", playerId=" + playerId + ", attribute=" + attribute + "]";
    }

}
