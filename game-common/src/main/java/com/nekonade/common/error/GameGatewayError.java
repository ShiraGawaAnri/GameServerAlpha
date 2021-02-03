package com.nekonade.common.error;


import com.nekonade.common.error.IServerError;

public enum GameGatewayError implements IServerError {
    GAME_GATEWAY_ERROR(-1,"网关逻辑错误,请稍后再试"),
    SERVER_LOGIC_UNAVAILABLE(101,"逻辑服务器不可用,请稍后再试"),
    SERVER_IM_UNAVAILABLE(103,"聊天服务器不可用,请稍后再试"),
    TOKEN_ILLEGAL(50001,"TOKEN非法"),
    TOKEN_EXPIRE(50002,"TOKEN已过期"),
    REPEATED_CONNECT(50003,"重复连接，可能异地登陆了"),
    ;
    private final int errorCode;
    private final String errorDesc;



    GameGatewayError(int errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorDesc() {
        return errorDesc;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("errorCode:").append(this.errorCode).append("; errorMsg:").append(this.errorDesc);
        return msg.toString();
    }
}
