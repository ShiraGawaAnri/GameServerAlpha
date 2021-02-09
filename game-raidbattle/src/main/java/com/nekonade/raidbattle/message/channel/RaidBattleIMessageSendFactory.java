package com.nekonade.raidbattle.message.channel;


import com.nekonade.network.param.game.common.GameMessagePackage;

public interface RaidBattleIMessageSendFactory {

    void sendMessage(GameMessagePackage gameMessagePackage, RaidBattleChannelPromise promise);
}
