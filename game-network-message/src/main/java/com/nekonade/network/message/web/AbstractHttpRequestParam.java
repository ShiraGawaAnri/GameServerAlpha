package com.nekonade.network.message.web;


import com.nekonade.network.message.errors.GameErrorException;
import com.nekonade.network.message.errors.IServerError;

public abstract class AbstractHttpRequestParam {
    protected IServerError error;

    public void checkParam() {
        haveError();
        if (error != null) {
            throw new GameErrorException.Builder(error).message("异常类:{}", this.getClass().getName()).build();
        }
    }
    protected abstract void haveError();
}
