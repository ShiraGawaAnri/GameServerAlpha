package com.nekonade.common.errors;

import lombok.Getter;
import lombok.Setter;

public class TokenException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private boolean expire;

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenException(String message) {
        super(message);
    }

}
