package com.nekonade.network.param.game.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameMessageHeader implements Cloneable {
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
    private EnumMessageType messageType;

    private HeaderAttribute attribute = new HeaderAttribute();

    @Override
    public GameMessageHeader clone() throws CloneNotSupportedException {

        return (GameMessageHeader) super.clone();
    }



}
