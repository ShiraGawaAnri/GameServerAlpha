package com.nekonade.neko.logic;


import com.nekonade.network.message.context.GatewayMessageContext;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.network.param.game.message.im.SendIMMsgRequest;
import com.nekonade.network.param.game.message.im.SendIMMsgeResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;

@GameMessageHandler
public class GameIMHandler {

    @GameMessageMapping(SendIMMsgRequest.class)
    public void sendMsg(SendIMMsgRequest request, GatewayMessageContext<PlayerManager> ctx) {
        String chat = request.getBodyObj().getChat();
        String sender = ctx.getPlayerManager().getPlayer().getNickName();
        SendIMMsgeResponse response = new SendIMMsgeResponse();
        response.getBodyObj().setChat(chat);
        response.getBodyObj().setSender(sender);
        ctx.broadcastMessage(response);
    }
}
