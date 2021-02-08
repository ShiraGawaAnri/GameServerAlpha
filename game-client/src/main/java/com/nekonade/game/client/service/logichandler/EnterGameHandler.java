package com.nekonade.game.client.service.logichandler;

import com.nekonade.game.client.command.IMClientCommand;
import com.nekonade.game.client.service.handler.GameClientChannelContext;
import com.nekonade.network.param.game.message.im.IMSendIMMsgeResponse;
import com.nekonade.network.param.game.message.im.SendIMMsgeResponse;
import com.nekonade.network.param.game.message.neko.BuyArenaChallengeTimesMsgResponse;
import com.nekonade.network.param.game.message.neko.EnterGameMsgResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GameMessageHandler
public class EnterGameHandler {

    private static final Logger logger = LoggerFactory.getLogger(EnterGameHandler.class);

    @GameMessageMapping(EnterGameMsgResponse.class)
    public void enterGameResponse(EnterGameMsgResponse response, GameClientChannelContext ctx) {
        logger.debug("进入游戏成功：{}", response.getBodyObj().getNickname());
        IMClientCommand.enteredGame = true;
    }

    @GameMessageMapping(BuyArenaChallengeTimesMsgResponse.class)
    public void buyArenaChallengeTimes(BuyArenaChallengeTimesMsgResponse response, GameClientChannelContext ctx) {
        logger.debug("购买竞技场挑战次数成功");
    }

    @GameMessageMapping(SendIMMsgeResponse.class)
    public void chatMsg(SendIMMsgeResponse response, GameClientChannelContext ctx) {
        logger.info("聊天信息-{}说：{}", response.getBodyObj().getSender(), response.getBodyObj().getChat());
    }

    @GameMessageMapping(IMSendIMMsgeResponse.class)
    public void chatMsgIM(IMSendIMMsgeResponse response, GameClientChannelContext ctx) {
        logger.info("聊天信息-{}说：{}", response.getBodyObj().getSender(), response.getBodyObj().getChat());
    }
}
