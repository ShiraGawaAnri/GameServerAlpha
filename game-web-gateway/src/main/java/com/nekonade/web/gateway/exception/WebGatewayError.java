package com.nekonade.web.gateway.exception;

import com.nekonade.network.message.errors.IServerError;

public enum WebGatewayError implements IServerError {
    UNKNOWN(-2, "网关服务器未知道异常"),
    TOO_MANY_GLOBAL_REQUEST(1,"Web网关全局请求过多"),
    TOO_MANY_USER_REQUEST(2,"Web网关个体用户请求过多"),
    ;
    private final int errorCode;
    private final String errorDesc;



    WebGatewayError(int errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorDesc() {
        return this.errorDesc;
    }

    @Override
    public String toString() {
        return "errorCode:" + errorCode + "; errorMsg:" + this.errorDesc;
    }
}
