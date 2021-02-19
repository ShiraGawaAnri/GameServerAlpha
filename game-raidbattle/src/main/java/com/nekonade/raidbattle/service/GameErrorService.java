package com.nekonade.raidbattle.service;

import com.nekonade.common.error.BasicException;
import com.nekonade.common.error.ErrorResponseEntity;
import com.nekonade.common.error.GameErrorException;
import com.nekonade.common.error.GameNotifyException;
import com.nekonade.common.error.code.GameErrorCode;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.GameMessageHeader;
import com.nekonade.network.param.game.common.IGameMessage;
import com.nekonade.network.param.game.message.neko.error.GameErrorMsgResponse;
import com.nekonade.network.param.game.message.neko.error.GameNotificationMsgResponse;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import com.nekonade.raidbattle.message.channel.AbstractRaidBattleChannelHandlerContext;
import com.nekonade.raidbattle.message.context.RaidBattleMessageContext;
import org.springframework.stereotype.Service;

@Service
public class GameErrorService {


    public AbstractJsonGameMessage returnGameErrorResponse(Throwable cause) {
        ErrorResponseEntity errorEntity = new ErrorResponseEntity();
        BasicException exception;
        int type = 0;
        if (cause instanceof GameErrorException) {
            exception = (GameErrorException) cause;
            type = 1;
        } else if (cause instanceof GameNotifyException) {
            exception = (GameNotifyException) cause;
            type = 2;
        } else {
            exception = GameErrorException.newBuilder(GameErrorCode.LogicError).build();
        }
        errorEntity.setErrorCode(exception.getError().getErrorCode());
        errorEntity.setErrorMsg(exception.getError().getErrorDesc());
        errorEntity.setData(exception.getData());
        AbstractJsonGameMessage response = null;
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
        return response;
    }

    public void returnGameErrorResponse(Throwable cause, RaidBattleMessageContext<RaidBattleManager> ctx) {
        ErrorResponseEntity errorEntity = new ErrorResponseEntity();
        BasicException exception;
        int type = 0;
        if (cause instanceof GameErrorException) {
            exception = (GameErrorException) cause;
            type = 1;
        } else if (cause instanceof GameNotifyException) {
            exception = (GameNotifyException) cause;
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