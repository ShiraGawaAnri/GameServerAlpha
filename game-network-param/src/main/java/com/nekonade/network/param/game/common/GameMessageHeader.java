package com.nekonade.network.param.game.common;

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

    public EnumMesasageType getMesasageType() {
        return mesasageType;
    }

    public void setMesasageType(EnumMesasageType mesasageType) {
        this.mesasageType = mesasageType;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public HeaderAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(HeaderAttribute attribute) {
        this.attribute = attribute;
    }

    public int getFromServerId() {
        return fromServerId;
    }

    public void setFromServerId(int fromServerId) {
        this.fromServerId = fromServerId;
    }

    public int getToServerId() {
        return toServerId;
    }

    public void setToServerId(int toServerId) {
        this.toServerId = toServerId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public long getClientSendTime() {
        return clientSendTime;
    }

    public void setClientSendTime(long clientSendTime) {
        this.clientSendTime = clientSendTime;
    }

    public long getServerSendTime() {
        return serverSendTime;
    }

    public void setServerSendTime(long serverSendTime) {
        this.serverSendTime = serverSendTime;
    }


    public int getClientSeqId() {
        return clientSeqId;
    }

    public void setClientSeqId(int clientSeqId) {
        this.clientSeqId = clientSeqId;
    }

   

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "GameMessageHeader [messageSize=" + messageSize + ", messageId=" + messageId + ", serviceId=" + serviceId + ", clientSendTime=" + clientSendTime + ", serverSendTime=" + serverSendTime + ", clientSeqId=" + clientSeqId + ", version=" + version + ", errorCode=" + errorCode + ", fromServerId=" + fromServerId + ", toServerId=" + toServerId + ", playerId=" + playerId + ", attribute=" + attribute + "]";
    }

}
