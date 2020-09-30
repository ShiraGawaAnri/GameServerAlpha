package com.nekonade.network.message.errors;

public interface IServerError {

    int getErrorCode();
    String getErrorDesc();
    
}
