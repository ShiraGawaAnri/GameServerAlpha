package com.nekonade.neko.logic;

import com.nekonade.common.dto.Player;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.network.message.manager.RaidBattleManager;
import com.nekonade.network.message.rpc.RPCEvent;
import com.nekonade.network.message.rpc.RPCEventContext;
import com.nekonade.network.param.game.message.neko.battle.rpc.JoinRaidBattleRPCRequest;
import com.nekonade.network.param.game.message.neko.battle.rpc.JoinRaidBattleRPCResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.rpc.ConsumeDiamondRPCRequest;
import com.nekonade.network.param.game.rpc.ConsumeDiamondRPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

@GameMessageHandler
public class RPCBusinessHandler {

    private static final Logger logger = LoggerFactory.getLogger(RPCBusinessHandler.class);

    @RPCEvent(ConsumeDiamondRPCRequest.class)
    public void consumDiamond(RPCEventContext<RaidBattleManager> ctx, ConsumeDiamondRPCRequest request) {
        logger.debug("收到扣钻石的rpc请求");
        ConsumeDiamondRPCResponse response = new ConsumeDiamondRPCResponse();
        ctx.sendResponse(response);
    }

    @RPCEvent(JoinRaidBattleRPCRequest.class)
    public void joinRaidBattle(RPCEventContext<PlayerManager> ctx, JoinRaidBattleRPCRequest request){
        logger.info("收到加入RaidBattle的请求 {}",request);
        PlayerManager data = ctx.getData();
        JoinRaidBattleRPCResponse response = new JoinRaidBattleRPCResponse();
        Player player = new Player();
        BeanUtils.copyProperties(data.getPlayer().clone(),player);
        response.getBodyObj().setPlayer(player);
        ctx.sendResponse(response);
    }
}
