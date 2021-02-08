package com.nekonade.common.error;


public enum GameCenterError implements IServerError {
    UNKNOW(-1, "用户中心服务未知异常"),
    SDK_VERFIY_ERROR(1, "sdk验证错误"),
    OPENID_IS_EMPTY(2, "openId为空"),
    OPENID_LEN_ERROR(21, "openId长度不对"),
    SDK_TOKEN_ERROR(3, "SDK token错误"),
    SDK_TOKEN_LEN_ERROR(31, "sdk token 长度不对"),
    NICKNAME_EXIST(4, "昵称已存在"),
    ZONE_ID_IS_EMPTY(5, "zoneId为空"),
    NICKNAME_IS_EMPTY(6, "昵称为空"),
    NICKNAME_LEN_ERROR(7, "昵称长度不对"),
    TOKEN_FAILED(8, "token错误"),
    NO_GAME_GATEWAY_INFO(9, "没有网关信息，无法连接游戏"),
    USERNAME_IS_EMPTY(10, "用户名为空"),
    PASSWORD_IS_EMPTY(11, "密码为空"),
    ILLEGAL_LOGIN_TYPE(12, "非法的登陆方式"),
    LOGIN_PASSWORD_ERROR(13, "登陆密码或用户名不正确"),
    DUPLICATE_CREATEPLAYER_ERROR(14, "已经创建过角色"),
    NOT_CREATEPLAYER_ERROR(15, "请先创建角色"),
    ;
    private final int errorCode;
    private final String errorDesc;


    GameCenterError(int errorCode, String errorDesc) {
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
