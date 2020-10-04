package com.nekonade.gamegateway.messages;

public enum MessageCode {
    ConnectConfirm(1, "连接认证"),
    Heartbeat(2, "心跳消息"),;
    private final int messageId;
    private final String desc;
    private String toString;

    MessageCode(int messageId, String desc) {
        this.messageId = messageId;
        this.desc = desc;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public String getDesc() {
        return this.desc;
    }

    @Override
    public String toString() {
        if (this.toString == null) {
            StringBuilder str = new StringBuilder();
            str.append("messageId:").append(this.messageId).append(",desc:").append(this.desc);
            this.toString = str.toString();
        }
        return this.toString;
    }


}
