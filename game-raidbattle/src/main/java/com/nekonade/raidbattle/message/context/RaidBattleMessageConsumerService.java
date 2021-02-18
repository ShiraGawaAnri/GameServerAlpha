package com.nekonade.raidbattle.message.context;

import com.nekonade.common.cloud.PlayerServiceInstance;
import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.raidbattle.message.channel.RaidBattleChannelConfig;
import com.nekonade.raidbattle.message.channel.RaidBattleChannelInitializer;
import com.nekonade.raidbattle.message.channel.RaidBattleIMessageSendFactory;
import com.nekonade.raidbattle.message.channel.RaidBattleMessageEventDispatchService;
import com.nekonade.raidbattle.message.rpc.RaidBattleRPCService;
import com.nekonade.network.param.game.GameMessageService;
import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageHeader;
import com.nekonade.network.param.game.common.GameMessagePackage;
import com.nekonade.network.param.game.common.IGameMessage;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class RaidBattleMessageConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(RaidBattleMessageConsumerService.class);
    private final EventExecutorGroup rpcWorkerGroup = new DefaultEventExecutorGroup(2);
    private RaidBattleIMessageSendFactory gameGatewayMessageSendFactory;// 默认实现的消息发送接口，GameChannel返回的消息通过此接口发送到kafka中
    private RaidBattleRPCService gameRpcSendFactory;
    @Autowired
    private RaidBattleChannelConfig serverConfig;// GameChannel的一些配置信息
    @Autowired
    private GameMessageService gameMessageService; // 消息管理类，负责管理根据消息id，获取对应的消息类实例
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate; // kafka客户端类
    private RaidBattleMessageEventDispatchService gameChannelService;// 消息事件分类发，负责将用户的消息发到相应的GameChannel之中。
    private GameEventExecutorGroup workerGroup;// 业务处理的线程池
    @Autowired
    private PlayerServiceInstance playerServiceInstance;
    @Autowired
    private ApplicationContext context;

    private AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void setMessageSendFactory(RaidBattleIMessageSendFactory messageSendFactory) {
        this.gameGatewayMessageSendFactory = messageSendFactory;
    }

    public RaidBattleMessageEventDispatchService getRaidBattleMessageEventDispatchService() {
        return this.gameChannelService;
    }

    public void start(RaidBattleChannelInitializer gameChannelInitializer, int localServerId) {
        workerGroup = new GameEventExecutorGroup(serverConfig.getWorkerThreads());
        gameGatewayMessageSendFactory = new RaidBattleGatewayMessageSendFactory(kafkaTemplate, serverConfig.getGatewayGameMessageTopic());
        gameRpcSendFactory = new RaidBattleRPCService(serverConfig.getRpcRequestGameMessageTopic(), serverConfig.getRpcResponseGameMessageTopic(), localServerId, playerServiceInstance, rpcWorkerGroup, kafkaTemplate);
        gameChannelService = new RaidBattleMessageEventDispatchService(context, workerGroup, gameGatewayMessageSendFactory, gameRpcSendFactory, gameChannelInitializer);
    }

    private void CheckInited(){
        if(gameChannelService == null){
            Thread t = Thread.currentThread();
            while (!atomicReference.compareAndSet(null, t)) {
                if(gameChannelService != null){
                    atomicReference.compareAndSet(t,null);
                }
            }
        }
    }

    @KafkaListener(topics = {"${game.channel.business-game-message-topic}" + "-" + "${game.server.config.server-id}"}, groupId = "${game.channel.topic-group-id}")
    public void consume(ConsumerRecord<String, byte[]> record) {
        CheckInited();
        IGameMessage gameMessage = this.getGameMessage(EnumMessageType.REQUEST, record.value());
        GameMessageHeader header = gameMessage.getHeader();
        String raidId = header.getAttribute().getRaidId();
        gameChannelService.fireReadMessage(raidId, gameMessage);
    }

    @KafkaListener(topics = {"${game.channel.rpc-request-game-message-topic}" + "-" + "${game.server.config.server-id}"}, groupId = "rpc-${game.channel.topic-group-id}")
    public void consumeRPCRequestMessage(ConsumerRecord<String, byte[]> record) {
        CheckInited();
        IGameMessage gameMessage = this.getGameMessage(EnumMessageType.RPC_REQUEST, record.value());
        gameChannelService.fireReadRPCRequest(gameMessage);
    }

    @KafkaListener(topics = {"${game.channel.rpc-response-game-message-topic}" + "-" + "${game.server.config.server-id}"}, groupId = "rpc-request-${game.channel.topic-group-id}")
    public void consumeRPCResponseMessage(ConsumerRecord<String, byte[]> record) {
        CheckInited();
        IGameMessage gameMessage = this.getGameMessage(EnumMessageType.RPC_RESPONSE, record.value());
        this.gameRpcSendFactory.recieveResponse(gameMessage);
    }

    private IGameMessage getGameMessage(EnumMessageType messageType, byte[] data) {
        CheckInited();
        GameMessagePackage gameMessagePackage = GameMessageInnerDecoder.readGameMessagePackage(data);
        logger.debug("RB收到消息,类型 {} - Header: {}", messageType, gameMessagePackage.getHeader());
        GameMessageHeader header = gameMessagePackage.getHeader();
        IGameMessage gameMessage = gameMessageService.getMessageInstance(messageType, header.getMessageId());
        gameMessage.read(gameMessagePackage.getBody());
        gameMessage.setHeader(header);
        gameMessage.getHeader().setMessageType(messageType);
        return gameMessage;
    }


}
