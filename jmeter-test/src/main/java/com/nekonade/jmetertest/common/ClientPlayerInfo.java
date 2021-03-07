package com.nekonade.jmetertest.common;

import com.nekonade.network.param.http.response.GameGatewayInfoMsg;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
public class ClientPlayerInfo {

    private String userName;
    private String password;
    private long playerId;
    private String token;
    private long userId;
    private GameGatewayInfoMsg gameGatewayInfoMsg;


}
