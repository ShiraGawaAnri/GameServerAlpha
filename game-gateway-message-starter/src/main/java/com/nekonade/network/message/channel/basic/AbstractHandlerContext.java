package com.nekonade.network.message.channel.basic;

import com.nekonade.network.param.game.common.IGameMessage;
import io.netty.util.concurrent.Promise;

public class AbstractHandlerContext {

    public void writeRPCMessage(IGameMessage msg, Promise<IGameMessage> promise){
    }
}
