package com.nekonade.network.message.channel.battle;

public interface RaidBattleChannelHandler {

    void exceptionCaught(AbstractRaidBattleChannelHandlerContext ctx, Throwable cause) throws Exception;
}
