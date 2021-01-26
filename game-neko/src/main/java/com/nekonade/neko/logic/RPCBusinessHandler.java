package com.nekonade.neko.logic;

import com.nekonade.dao.db.entity.manager.ArenaManager;
import com.nekonade.network.message.rpc.RPCEvent;
import com.nekonade.network.message.rpc.RPCEventContext;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.rpc.ConsumeDiamonRPCResponse;
import com.nekonade.network.param.game.rpc.ConsumeDiamondRPCRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GameMessageHandler
public class RPCBusinessHandler {
    private Logger logger = LoggerFactory.getLogger(RPCBusinessHandler.class);
    @RPCEvent(ConsumeDiamondRPCRequest.class)
    public void consumDiamond(RPCEventContext<ArenaManager> ctx, ConsumeDiamondRPCRequest request) {
         logger.debug("收到扣钻石的rpc请求");
         ConsumeDiamonRPCResponse response = new ConsumeDiamonRPCResponse();
         ctx.sendResponse(response);
    }
}
