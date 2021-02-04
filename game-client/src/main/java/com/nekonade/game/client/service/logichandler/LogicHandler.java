package com.nekonade.game.client.service.logichandler;


import com.nekonade.common.dto.Mail;
import com.nekonade.common.model.PageResult;
import com.nekonade.game.client.service.handler.GameClientChannelContext;
import com.nekonade.network.param.game.message.neko.*;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

@GameMessageHandler
public class LogicHandler {

    private static final Logger logger = LoggerFactory.getLogger(LogicHandler.class);

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

    @GameMessageMapping(GetMailBoxMsgResponse.class)
    public void getMailBoxMsgResponse(GetMailBoxMsgResponse response,GameClientChannelContext ctx){
        GetMailBoxMsgResponse.PageResult responseBodyObj = response.getBodyObj();
        PageResult<Mail> mail = new PageResult<>();
        BeanUtils.copyProperties(responseBodyObj,mail);
        logger.info("玩家邮件信息{}",mail);
    }

    @GameMessageMapping(CreateBattleMsgResponse.class)
    public void createBattleMsgResponse(CreateBattleMsgResponse response,GameClientChannelContext ctx){
        CreateBattleMsgResponse.RaidBattle bodyObj = response.getBodyObj();
        logger.info("创建战斗信息{}",response.bodyToString());
    }
}
