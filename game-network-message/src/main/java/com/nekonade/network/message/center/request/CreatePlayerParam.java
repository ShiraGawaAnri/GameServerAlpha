package com.nekonade.center.messages.request;

import com.nekonade.center.messages.GameCenterError;
import com.nekonade.network.message.web.AbstractHttpRequestParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

public class CreatePlayerParam extends AbstractHttpRequestParam {

    @Getter
    @Setter
    private String nickName;
    
    @Override
    protected void haveError() {
         if (StringUtils.isEmpty(nickName)) {
            this.error = GameCenterError.NICKNAME_IS_EMPTY;
        } else {
            int len = nickName.length();
            if (len < 2 || len > 10) {
                this.error = GameCenterError.NICKNAME_LEN_ERROR;
            }
        }
    }



}
