package com.nekonade.game.client.service.logichandler;


import com.nekonade.game.client.service.handler.GameClientChannelContext;
import com.nekonade.network.param.game.message.neko.*;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GameMessageHandler
public class LogicHandler {

    private Logger logger = LoggerFactory.getLogger(LogicHandler.class);

    @GameMessageMapping(GetPlayerByIdMsgResponse.class)
    public void getPlayerByIdResponse(GetPlayerByIdMsgResponse response, GameClientChannelContext ctx){
        logger.info("查询指定PlayerId信息{}",response.bodyToString());
    }

    @GameMessageMapping(GetPlayerSelfMsgResponse.class)
    public void getPlayerSelfMsgResponse(GetPlayerSelfMsgResponse response, GameClientChannelContext ctx){
        logger.info("查询自身信息{}",response.bodyToString());
    }

    @GameMessageMapping(GetInventoryMsgResponse.class)
    public void getInventoryMsgResponse(GetInventoryMsgResponse response, GameClientChannelContext ctx){
        logger.info("查询仓库{}",response.bodyToString());
    }

    @GameMessageMapping(GetStaminaMsgResponse.class)
    public void getStaminaMsgResponse(GetStaminaMsgResponse response,GameClientChannelContext ctx){
        logger.info("查询疲劳{}",response.bodyToString());
    }

    @GameMessageMapping(GetArenaPlayerListMsgResponse.class)
    public void getArenaPlayerListMsgResponse(GetArenaPlayerListMsgResponse response,GameClientChannelContext ctx){
        logger.info("查询战场玩家列表{}",response.bodyToString());
    }

    @GameMessageMapping(LevelUpMsgResponse.class)
    public void levelUpdateMsgResponse(LevelUpMsgResponse response,GameClientChannelContext ctx){
        logger.info("玩家升级消息{}",response.bodyToString());
    }
}
