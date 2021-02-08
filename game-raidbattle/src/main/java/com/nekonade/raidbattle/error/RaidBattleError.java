package com.nekonade.raidbattle.error;
import com.nekonade.common.error.IServerError;

public enum RaidBattleError implements IServerError {

    SERVER_ERROR(101,"服务器异常"),
    ;
    private int errorCode;
    private String errorDesc;

    private RaidBattleError(int errorCode, String errorDesc) {
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
