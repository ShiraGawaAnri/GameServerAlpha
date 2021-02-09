package com.nekonade.raidbattle;

import com.nekonade.network.message.context.GatewayMessageConsumerService;
import com.nekonade.network.message.context.ServerConfig;
import com.nekonade.network.message.context.battle.RaidBattleMessageConsumerService;
import com.nekonade.network.message.handler.GameChannelIdleStateHandler;
import com.nekonade.network.message.handler.battle.RaidBattleChannelIdleStateHandler;
import com.nekonade.network.param.game.messagedispatcher.DispatchGameMessageService;
import com.nekonade.raidbattle.handler.RaidBattleBusinessDispatchHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {"com.nekonade"})
@EnableMongoRepositories(basePackages = {"com.nekonade"})
public class GameRaidBattle {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(GameRaidBattle.class, args);
        ServerConfig serverConfig = context.getBean(ServerConfig.class);
        DispatchGameMessageService.scanGameMessages(context,serverConfig.getServiceId(),"com.nekonade");
//        GatewayMessageConsumerService gatewayMessageConsumerService = context.getBean(GatewayMessageConsumerService.class);//获取网关消息监听实例
//        gatewayMessageConsumerService.start((gameChannel) -> {//启动网关消息监听，并初始化GameChannelHandler
//            // 初始化channel
//            gameChannel.getChannelPiple().addLast(new GameChannelIdleStateHandler(120, 120, 100));
//            gameChannel.getChannelPiple().addLast(new RaidBattleBusinessDispatchHandler(context));
//        },serverConfig.getServerId());

        RaidBattleMessageConsumerService consumerService = context.getBean(RaidBattleMessageConsumerService.class);
        consumerService.start((gameChannel)->{
            gameChannel.getChannelPipeLine().addLast(new RaidBattleChannelIdleStateHandler(120, 120, 100));
            new RaidBattleBusinessDispatchHandler(context);
            gameChannel.getChannelPipeLine().addLast(new RaidBattleBusinessDispatchHandler(context));
        },serverConfig.getServerId());

    }
}
