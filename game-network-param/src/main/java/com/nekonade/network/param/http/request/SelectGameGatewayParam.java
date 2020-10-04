package com.nekonade.network.param.http.request;

import com.nekonade.common.utils.CommonField;
import com.nekonade.network.param.error.GameCenterError;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class SelectGameGatewayParam extends AbstractHttpRequestParam {
    private String username;
    private String openId; // 第三方用户唯一id
    private long playerId; // 角色id
    private long userId; // 用户id
    private String zoneId = "0"; // 选择的区id

    @Override
    protected void haveError() {
        if (StringUtils.isEmpty(openId)) {
            this.error = GameCenterError.OPENID_IS_EMPTY;
        } else if (openId.length() > CommonField.OPEN_ID_LENGTH) {
            this.error = GameCenterError.OPENID_LEN_ERROR;
        } else if (StringUtils.isEmpty(this.zoneId)) {
            this.error = GameCenterError.ZONE_ID_IS_EMPTY;
        }
    }
}
