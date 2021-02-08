package com.nekonade.common.error;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseEntity {

    private int errorCode;

    private String errorMsg;

    private Object data;
}
