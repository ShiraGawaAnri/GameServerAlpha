package com.nekonade.neko.logic;

import com.nekonade.common.dto.PlayerDTO;
import com.nekonade.network.message.context.GatewayMessageConsumerService;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.network.message.rpc.RPCEvent;
import com.nekonade.network.message.rpc.RPCEventContext;
import com.nekonade.network.param.game.message.battle.rpc.JoinRaidBattleRPCRequest;
import com.nekonade.network.param.game.message.battle.rpc.JoinRaidBattleRPCResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.rpc.ConsumeDiamondRPCRequest;
import com.nekonade.network.param.game.rpc.ConsumeDiamondRPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

@GameMessageHandler
public class RPCBusinessHandler {

    private static final Logger logger = LoggerFactory.getLogger(RPCBusinessHandler.class);

    @Autowired
    private GatewayMessageConsumerService gatewayMessageConsumerService;

    @RPCEvent(ConsumeDiamondRPCRequest.class)
    public void consumDiamond(RPCEventContext<PlayerManager> ctx, ConsumeDiamondRPCRequest request) {
        logger.debug("收到扣钻石的rpc请求");
        ConsumeDiamondRPCResponse response = new ConsumeDiamondRPCResponse();
        ctx.sendResponse(response);
    }

    @RPCEvent(JoinRaidBattleRPCRequest.class)
    public void joinRaidBattle(RPCEventContext<PlayerManager> ctx, JoinRaidBattleRPCRequest request){
        logger.info("收到加入RaidBattle的请求 {}",request);
        PlayerManager data = ctx.getData();
        JoinRaidBattleRPCResponse response = new JoinRaidBattleRPCResponse();
        response.wrapResponse(request);
        PlayerDTO playerDTO = new PlayerDTO();
        BeanUtils.copyProperties(data.getPlayer(), playerDTO);
        response.getBodyObj().setPlayer(playerDTO);
        ctx.sendResponse(response);
    }
}
