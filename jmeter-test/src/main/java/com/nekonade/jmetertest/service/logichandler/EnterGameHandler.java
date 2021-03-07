package com.nekonade.jmetertest.service.logichandler;

import com.nekonade.jmetertest.StressTesting;
import com.nekonade.jmetertest.service.GameClientBoot;
import com.nekonade.jmetertest.service.handler.GameClientChannelContext;
import com.nekonade.network.param.game.message.im.IMSendIMMsgeResponse;
import com.nekonade.network.param.game.message.im.SendIMMsgeResponse;
import com.nekonade.network.param.game.message.neko.DoBuyArenaChallengeTimesMsgResponse;
import com.nekonade.network.param.game.message.neko.DoEnterGameMsgResponse;
import com.nekonade.network.param.game.message.neko.GetPlayerSelfMsgRequest;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GameMessageHandler
public class EnterGameHandler {

    public static GameClientBoot gameClientBoot;

    static {
        gameClientBoot = StressTesting.gameClientBoot;
    }

    private static final Logger logger = LoggerFactory.getLogger(EnterGameHandler.class);

    @GameMessageMapping(DoEnterGameMsgResponse.class)
    public void enterGameResponse(DoEnterGameMsgResponse response, GameClientChannelContext ctx) {
        StressTesting.playerInfo.setEntered(true);
        logger.info("进入游戏成功：{}", response.getBodyObj().getNickname());
        GetPlayerSelfMsgRequest getPlayerSelfMsgRequest = new GetPlayerSelfMsgRequest();
        gameClientBoot.getChannel().writeAndFlush(getPlayerSelfMsgRequest);
    }

    @GameMessageMapping(DoBuyArenaChallengeTimesMsgResponse.class)
    public void buyArenaChallengeTimes(DoBuyArenaChallengeTimesMsgResponse response, GameClientChannelContext ctx) {
        logger.info("购买竞技场挑战次数成功");
    }

    @GameMessageMapping(SendIMMsgeResponse.class)
    public void chatMsg(SendIMMsgeResponse response, GameClientChannelContext ctx) {
        logger.info("聊天信息-{}说：{}", response.getBodyObj().getSender(), response.getBodyObj().getText());
    }

    @GameMessageMapping(IMSendIMMsgeResponse.class)
    public void chatMsgIM(IMSendIMMsgeResponse response, GameClientChannelContext ctx) {
        logger.info("聊天信息-{}说：{}", response.getBodyObj().getSender(), response.getBodyObj().getChat());
    }
}
