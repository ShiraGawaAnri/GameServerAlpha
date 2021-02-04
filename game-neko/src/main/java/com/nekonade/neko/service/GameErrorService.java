package com.nekonade.neko.service;


import com.nekonade.common.error.GameErrorException;
import com.nekonade.common.error.ErrorResponseEntity;
import com.nekonade.network.message.context.GatewayMessageContext;
import com.nekonade.network.message.manager.GameErrorCode;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.network.param.game.message.neko.error.GameErrorMsgResponse;
import org.springframework.stereotype.Service;

@Service
public class GameErrorService {

    public void returnGameErrorResponse(Throwable cause, GatewayMessageContext<PlayerManager> ctx){
        GameErrorException exception;
        if(cause instanceof GameErrorException){
            exception = (GameErrorException) cause;
        }else{
            exception = GameErrorException.newBuilder(GameErrorCode.LogicError).build();
        }
        GameErrorMsgResponse response = new GameErrorMsgResponse();
        ErrorResponseEntity errorEntity = new ErrorResponseEntity();
        errorEntity.setErrorCode(exception.getError().getErrorCode());
        errorEntity.setErrorMsg(exception.getError().getErrorDesc());
        response.getBodyObj().setError(errorEntity);
        ctx.sendMessage(response);
    }
}
