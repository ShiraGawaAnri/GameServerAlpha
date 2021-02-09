package com.nekonade.network.message.channel.battle;

import com.nekonade.network.param.game.common.IGameMessage;
import io.netty.util.concurrent.Promise;

public interface RaidBattleChannelOutboundHandler extends RaidBattleChannelHandler {

    void writeAndFlush(AbstractRaidBattleChannelHandlerContext ctx, IGameMessage msg, RaidBattleChannelPromise promise) throws Exception;

    void writeRPCMessage(AbstractRaidBattleChannelHandlerContext ctx, IGameMessage gameMessage, Promise<IGameMessage> callback);

    void close(AbstractRaidBattleChannelHandlerContext ctx, RaidBattleChannelPromise promise);

}
