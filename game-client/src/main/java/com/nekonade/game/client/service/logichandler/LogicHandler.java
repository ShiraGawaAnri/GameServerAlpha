package com.nekonade.game.client.service.logichandler;


import com.nekonade.game.client.service.handler.GameClientChannelContext;
import com.nekonade.network.param.game.message.neko.GetInventoryMsgResponse;
import com.nekonade.network.param.game.message.neko.GetPlayerByIdMsgResponse;
import com.nekonade.network.param.game.message.neko.GetPlayerSelfMsgResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GameMessageHandler
public class LogicHandler {

    private Logger logger = LoggerFactory.getLogger(LogicHandler.class);

    @GameMessageMapping(GetPlayerByIdMsgResponse.class)
    public void getPlayerByIdResponse(GetPlayerByIdMsgResponse response, GameClientChannelContext ctx){
        logger.info("查询角色信息回复{}",response.bodyToString());
    }

    @GameMessageMapping(GetPlayerSelfMsgResponse.class)
    public void getPlayerSelfMsgResponse(GetPlayerSelfMsgResponse response, GameClientChannelContext ctx){
        logger.info("查询自身信息回复{}",response.bodyToString());
    }

    @GameMessageMapping(GetInventoryMsgResponse.class)
    public void getInventoryMsgResponse(GetInventoryMsgResponse response, GameClientChannelContext ctx){
        logger.info("查询仓库{}",response);
    }
}
