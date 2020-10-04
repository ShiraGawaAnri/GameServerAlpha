package com.nekonade.web.gateway.exception;


import com.nekonade.common.error.IServerError;

public enum WebGatewayError implements IServerError {
    UNKNOWN(500, "网关服务器预料外异常"),
    TOO_MANY_GLOBAL_REQUEST(501, "Web网关全局请求过多"),
    TOO_MANY_USER_REQUEST(502, "Web网关个体用户请求过多"),
    TOKEN_EMPTY(403, "必须携带TOKEN");
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
