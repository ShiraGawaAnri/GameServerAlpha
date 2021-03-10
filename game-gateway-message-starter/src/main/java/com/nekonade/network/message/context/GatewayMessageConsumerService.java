package com.nekonade.network.message.context;

import com.alibaba.nacos.common.utils.ConcurrentHashSet;
import com.nekonade.common.cloud.PlayerServiceInstance;
import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.network.message.channel.GameChannelConfig;
import com.nekonade.network.message.channel.GameChannelInitializer;
import com.nekonade.network.message.channel.GameMessageEventDispatchService;
import com.nekonade.network.message.channel.IMessageSendFactory;
import com.nekonade.network.message.rpc.GameRPCService;
import com.nekonade.network.param.game.GameMessageService;
import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.common.gameMessage.EnumMessageType;
import com.nekonade.common.gameMessage.GameMessageHeader;
import com.nekonade.common.gameMessage.GameMessagePackage;
import com.nekonade.common.gameMessage.IGameMessage;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class GatewayMessageConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(GatewayMessageConsumerService.class);

    private final EventExecutorGroup rpcWorkerGroup = new DefaultEventExecutorGroup(2);

    private final GameEventExecutorGroup clearHashMapGroup = new GameEventExecutorGroup(1);

    private IMessageSendFactory gameGatewayMessageSendFactory;// 默认实现的消息发送接口，GameChannel返回的消息通过此接口发送到kafka中

    private GameRPCService gameRpcSendFactory;


    @Autowired
    private ApplicationContext context;
    @Autowired
    private GameChannelConfig serverConfig;// GameChannel的一些配置信息
    @Autowired
    private GameMessageService gameMessageService; // 消息管理类，负责管理根据消息id，获取对应的消息类实例
    @Autowired
    private PlayerServiceInstance playerServiceInstance;
    @Resource(name = "CustomKafkaTemplate")
    private KafkaTemplate<String, byte[]> kafkaTemplate; // kafka客户端类
    @Resource
    private KafkaListenerEndpointRegistry registry;

    private GameMessageEventDispatchService gameChannelService;// 消息事件分类发，负责将用户的消息发到相应的GameChannel之中。
    private GameEventExecutorGroup workerGroup;// 业务处理的线程池

    private final AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public GameMessageEventDispatchService getGameMessageEventDispatchService() {
        return this.gameChannelService;
    }

    public void start(GameChannelInitializer gameChannelInitializer, int localServerId) {
        workerGroup = new GameEventExecutorGroup(serverConfig.getWorkerThreads());
        gameGatewayMessageSendFactory = new GameGatewayMessageSendFactory(kafkaTemplate, serverConfig.getGatewayGameMessageTopic());
        gameRpcSendFactory = new GameRPCService(serverConfig.getRpcRequestGameMessageTopic(), serverConfig.getRpcResponseGameMessageTopic(), localServerId, playerServiceInstance, rpcWorkerGroup, kafkaTemplate);
        gameChannelService = new GameMessageEventDispatchService(context, workerGroup, gameGatewayMessageSendFactory, gameRpcSendFactory, gameChannelInitializer);
        clearHashMapGroup.scheduleWithFixedDelay(()->{
            if(consumeKeys.size() >= 100){
                consumeKeys.clear();
            }
        },60,60, TimeUnit.SECONDS);
        if(!registry.getListenerContainer("default-request").isRunning()){
            registry.getListenerContainer("default-request").start();
        }
        if(!registry.getListenerContainer("rpc-request").isRunning()){
            registry.getListenerContainer("rpc-request").start();
        }
        if(!registry.getListenerContainer("rpc-response").isRunning()){
            registry.getListenerContainer("rpc-response").start();
        }
    }

    private final Map<String,Boolean> consumeKeys = new ConcurrentHashMap<>();

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

    @KafkaListener(id = "default-request",topics = {"${game.channel.business-game-message-topic}" + "-" + "${game.server.config.server-id}"}, groupId = "${game.channel.topic-group-id}",containerFactory = "delayContainerFactory")
    public void consume(ConsumerRecord<byte[], byte[]> record) {
        String key = new String(record.key());
        Boolean flag = consumeKeys.putIfAbsent(key, true);
        if(flag != null){
            return;
        }
        IGameMessage gameMessage = this.getGameMessage(EnumMessageType.REQUEST, record.value());
        GameMessageHeader header = gameMessage.getHeader();
        gameChannelService.fireReadMessage(header.getPlayerId(), gameMessage);
    }

    @KafkaListener(id = "rpc-request",topics = {"${game.channel.rpc-request-game-message-topic}" + "-" + "${game.server.config.server-id}"}, groupId = "rpc-${game.channel.topic-group-id}",containerFactory = "delayContainerFactory")
    public void consumeRPCRequestMessage(ConsumerRecord<byte[], byte[]> record) {
        IGameMessage gameMessage = this.getGameMessage(EnumMessageType.RPC_REQUEST, record.value());
        gameChannelService.fireReadRPCRequest(gameMessage);
    }

    @KafkaListener(id = "rpc-response",topics = {"${game.channel.rpc-response-game-message-topic}" + "-" + "${game.server.config.server-id}"}, groupId = "rpc-request-${game.channel.topic-group-id}",containerFactory = "delayContainerFactory")
    public void consumeRPCResponseMessage(ConsumerRecord<byte[], byte[]> record) {
        IGameMessage gameMessage = this.getGameMessage(EnumMessageType.RPC_RESPONSE, record.value());
        this.gameRpcSendFactory.recieveResponse(gameMessage);
    }

    private IGameMessage getGameMessage(EnumMessageType messageType, byte[] data) {
        GameMessagePackage gameMessagePackage = GameMessageInnerDecoder.readGameMessagePackageV2(data);
        logger.debug("收到消息,类型 {} - Header: {}", messageType, gameMessagePackage.getHeader());
        GameMessageHeader header = gameMessagePackage.getHeader();
        IGameMessage gameMessage = gameMessageService.getMessageInstance(messageType, header.getMessageId());
        gameMessage.read(gameMessagePackage.getBody());
        gameMessage.setHeader(header);
        gameMessage.getHeader().setMessageType(messageType);
        return gameMessage;
    }
}
