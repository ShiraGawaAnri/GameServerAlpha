package com.nekonade.network.param.message;

import com.nekonade.common.error.IServerError;

public enum GatewayMessageCode implements IServerError {
    ConnectConfirm(1, "连接认证"),
    Heartbeat(2, "心跳消息"),
    EnterGame(201,"进入游戏"),
    WaitLines(10,"限流登陆"),
    RequestRefuse(11,"请求已拒绝"),
    RequestFunctionMaintenance(12,"请求的功能正在维护中")
    ;
    private final int messageId;
    private final String desc;

    GatewayMessageCode(int messageId, String desc) {
        this.messageId = messageId;
        this.desc = desc;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getDesc() {
        return desc;
    }


    @Override
    public int getErrorCode() {
        return messageId;
    }

    @Override
    public String getErrorDesc() {
        return desc;
    }
}
