package com.nekonade.center.messages.request;

import com.nekonade.center.messages.GameCenterError;
import com.nekonade.network.message.web.AbstractHttpRequestParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class LoginParam extends AbstractHttpRequestParam {
    private int loginType;// 登陆类型：1 用户名密码；2 第三方sdk
    private String userName;
    private String password;
    private String openId;
    private String token;

    private String loginIp;

    public boolean isUserNameLogin() {
        return this.loginType == 1;
    }


    @Override
    protected void haveError() {
        if(this.loginType == 1) {
            if(StringUtils.isEmpty(userName)) {
                this.error = GameCenterError.USERNAME_NULL;
            } else if(userName.length() > 32) {
                this.error = GameCenterError.USERNAME_LENGTH_ERROR;
            } else if(StringUtils.isEmpty(password)) {
                this.error = GameCenterError.PASSWORD_NULL;
            } else if(password.length() > 32) {
                this.error = GameCenterError.PASSWORD_LENGTH_ERROR;
            }
        } else if(this.loginType == 2) {
            if (StringUtils.isEmpty(openId)) {
                this.error = GameCenterError.OPENID_IS_EMPTY;
            } else if (openId.length() > 128) {
                this.error = GameCenterError.OPENID_LEN_ERROR;
            } else if (StringUtils.isEmpty(token)) {
                this.error = GameCenterError.SDK_TOKEN_ERROR;
            } else if (token.length() > 128) {
                this.error = GameCenterError.SDK_TOKEN_LEN_ERROR;
            }
        } else {
            this.error = GameCenterError.LOGIN_TYPE_ERROR;
        }
        

    }


}
