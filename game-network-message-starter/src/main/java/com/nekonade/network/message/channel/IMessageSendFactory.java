package com.nekonade.network.message.channel;


import com.nekonade.network.param.game.common.GameMessagePackage;

public interface IMessageSendFactory {

    void sendMessage(GameMessagePackage gameMessagePackage, GameChannelPromise promise);
}
