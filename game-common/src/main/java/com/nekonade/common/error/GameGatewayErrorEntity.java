package com.nekonade.common.error;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameGatewayErrorEntity {

    private int errorCode;

    private String errorMsg;
}
