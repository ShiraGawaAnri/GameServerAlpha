package com.nekonade.network.message.channel.battle;


import com.nekonade.network.param.game.common.GameMessagePackage;

public interface RaidBattleIMessageSendFactory {

    void sendMessage(GameMessagePackage gameMessagePackage, RaidBattleChannelPromise promise);
}
