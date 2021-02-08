package com.nekonade.neko.service;


import com.nekonade.common.error.BasicException;
import com.nekonade.common.error.ErrorResponseEntity;
import com.nekonade.common.error.GameErrorException;
import com.nekonade.common.error.GameNotification;
import com.nekonade.network.message.context.GatewayMessageContext;
import com.nekonade.network.message.manager.GameErrorCode;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.message.neko.error.GameErrorMsgResponse;
import com.nekonade.network.param.game.message.neko.error.GameNotificationMsgResponse;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Service
public class GameErrorService {

//    public <T> void returnGameErrorResponse(Throwable cause, GatewayMessageContext<PlayerManager> ctx,Class<T> exp){
//        T exception;
//        if(cause instanceof T){
//            exception = (T) cause;
//        }else{
//            exception = T.newBuilder(GameErrorCode.LogicError).build();
//        }
//        GameErrorMsgResponse response = new GameErrorMsgResponse();
//        ErrorResponseEntity errorEntity = new ErrorResponseEntity();
//        errorEntity.setErrorCode(exception.getError().getErrorCode());
//        errorEntity.setErrorMsg(exception.getError().getErrorDesc());
//        errorEntity.setData(exception.getData());
//        response.getBodyObj().setError(errorEntity);
//        ctx.sendMessage(response);
//    }

    public void returnGameErrorResponse(Throwable cause, GatewayMessageContext<PlayerManager> ctx) {
        ErrorResponseEntity errorEntity = new ErrorResponseEntity();
        BasicException exception;
        int type = 0;
        if (cause instanceof GameErrorException) {
            exception = (GameErrorException) cause;
            type = 1;
        } else if (cause instanceof GameNotification) {
            exception = (GameNotification) cause;
            type = 2;
        } else {
            exception = GameErrorException.newBuilder(GameErrorCode.LogicError).build();
        }
        errorEntity.setErrorCode(exception.getError().getErrorCode());
        errorEntity.setErrorMsg(exception.getError().getErrorDesc());
        errorEntity.setData(exception.getData());
        AbstractJsonGameMessage response;
        switch (type){
            default:
            case 0:
            case 1:
                response = new GameErrorMsgResponse();
                ((GameErrorMsgResponse)response).getBodyObj().setError(errorEntity);
                break;
            case 2:
                response = new GameNotificationMsgResponse();
                ((GameNotificationMsgResponse)response).getBodyObj().setError(errorEntity);
                break;
        }
        ctx.sendMessage(response);
    }
}