package com.nekonade.game.client.service.logichandler;


import com.nekonade.common.dto.Mail;
import com.nekonade.common.dto.RaidBattle;
import com.nekonade.common.model.PageResult;
import com.nekonade.game.client.common.PlayerInfo;
import com.nekonade.game.client.common.RaidBattleInfo;
import com.nekonade.game.client.service.handler.GameClientChannelContext;
import com.nekonade.network.param.game.message.neko.*;
import com.nekonade.network.param.game.message.neko.battle.JoinRaidBattleMsgResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

@GameMessageHandler
public class LogicHandler {

    private static final Logger logger = LoggerFactory.getLogger(LogicHandler.class);

    @Autowired
    private PlayerInfo playerInfo;

    @Autowired
    private RaidBattleInfo raidBattleInfo;

    @GameMessageMapping(GetPlayerByIdMsgResponse.class)
    public void getPlayerByIdResponse(GetPlayerByIdMsgResponse response, GameClientChannelContext ctx) {
        GetPlayerByIdMsgResponse.ResponseBody bodyObj = response.getBodyObj();
        logger.info("查询指定PlayerId信息{}", response.bodyToString());
    }

    @GameMessageMapping(GetPlayerSelfMsgResponse.class)
    public void getPlayerSelfMsgResponse(GetPlayerSelfMsgResponse response, GameClientChannelContext ctx) {
        GetPlayerSelfMsgResponse.ResponseBody bodyObj = response.getBodyObj();
        BeanUtils.copyProperties(bodyObj,playerInfo);
        logger.info("查询自身信息{}", response.bodyToString());
    }

    @GameMessageMapping(GetInventoryMsgResponse.class)
    public void getInventoryMsgResponse(GetInventoryMsgResponse response, GameClientChannelContext ctx) {
        logger.info("查询仓库{}", response.bodyToString());
    }

    @GameMessageMapping(GetStaminaMsgResponse.class)
    public void getStaminaMsgResponse(GetStaminaMsgResponse response, GameClientChannelContext ctx) {
        logger.info("查询疲劳{}", response.bodyToString());
    }

    @GameMessageMapping(GetArenaPlayerListMsgResponse.class)
    public void getArenaPlayerListMsgResponse(GetArenaPlayerListMsgResponse response, GameClientChannelContext ctx) {
        logger.info("查询战场玩家列表{}", response.bodyToString());
    }

    @GameMessageMapping(LevelUpMsgResponse.class)
    public void levelUpdateMsgResponse(LevelUpMsgResponse response, GameClientChannelContext ctx) {
        logger.info("玩家升级消息{}", response.bodyToString());
    }

    @GameMessageMapping(GetMailBoxMsgResponse.class)
    public void getMailBoxMsgResponse(GetMailBoxMsgResponse response, GameClientChannelContext ctx) {
        GetMailBoxMsgResponse.PageResult responseBodyObj = response.getBodyObj();
        PageResult<Mail> mail = new PageResult<>();
        BeanUtils.copyProperties(responseBodyObj, mail);
        logger.info("玩家邮件信息{}", mail);
    }

    @GameMessageMapping(CreateBattleMsgResponse.class)
    public void createBattleMsgResponse(CreateBattleMsgResponse response, GameClientChannelContext ctx) {
        long endNano = System.nanoTime();
        logger.info("END NANO TIME: {}",endNano);
        CreateBattleMsgResponse.RaidBattle bodyObj = response.getBodyObj();
        BeanUtils.copyProperties(bodyObj,raidBattleInfo);
        logger.info("战斗信息返回 \r\nRaidId {} \r\n{}",raidBattleInfo.getRaidId(), response.bodyToString());
    }

    @GameMessageMapping(JoinRaidBattleMsgResponse.class)
    public void joinRaidBattleMsgResponse(JoinRaidBattleMsgResponse response,GameClientChannelContext ctx){
        RaidBattle raidBattle = new RaidBattle();
        BeanUtils.copyProperties(response.getBodyObj(),raidBattle);
        logger.info("加入战斗结果返回 \r\nRaidId {} \r\n{}",raidBattle.getRaidId(), response.bodyToString());
    }
}
