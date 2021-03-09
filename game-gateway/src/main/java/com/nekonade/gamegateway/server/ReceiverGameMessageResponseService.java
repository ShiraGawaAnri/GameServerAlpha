package com.nekonade.gamegateway.server;

import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.gamegateway.common.GatewayServerConfig;
import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.common.gameMessage.GameMessagePackage;
import io.netty.channel.Channel;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName: GameMessageConsume
 * @Description: 接收业务服务返回的消息，并发送到客户端
 * @author: wgs
 * @date: 2019年5月15日 上午9:17:48
 */
@Service
public class ReceiverGameMessageResponseService {
    private static final Logger logger = LoggerFactory.getLogger(ReceiverGameMessageResponseService.class);
    @Autowired
    private GatewayServerConfig gatewayServerConfig;
    @Autowired
    private ChannelService channelService;
    private final GameEventExecutorGroup clearHashMapGroup = new GameEventExecutorGroup(1);

    private final Map<String,Boolean> consumeKeys = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        logger.info("监听消息接收业务消息topic:{}", gatewayServerConfig.getGatewayGameMessageTopic());
        clearHashMapGroup.scheduleWithFixedDelay(()->{
            if(consumeKeys.size() >= 1000){
                consumeKeys.clear();
            }
        },60,60, TimeUnit.SECONDS);
    }

    @KafkaListener(topics = {"${game.gateway.server.config.gateway-game-message-topic}"}, groupId = "${game.gateway.server.config.server-id}")
    public void receiver(ConsumerRecord<String, byte[]> record) {
        GameMessagePackage gameMessagePackage = GameMessageInnerDecoder.readGameMessagePackageV2(record.value());
        Long playerId = gameMessagePackage.getHeader().getPlayerId();//从包头中获取这个消息包归属的playerId
        Channel channel = channelService.getChannel(playerId);//根据playerId找到这个客户端的连接Channel
        if (channel != null) {
            channel.writeAndFlush(gameMessagePackage);//给客户端返回消息
        }
    }

    @KafkaListener(topics = {"${game.gateway.server.config.rb-gateway-game-message-topic}"}, groupId = "${game.gateway.server.config.server-id}")
    public void raidBattleReceiver(ConsumerRecord<String, byte[]> record) {
        GameMessagePackage gameMessagePackage = GameMessageInnerDecoder.readGameMessagePackageV2(record.value());
        Long playerId = gameMessagePackage.getHeader().getPlayerId();//从包头中获取这个消息包归属的playerId
        Channel channel = channelService.getChannel(playerId);//根据playerId找到这个客户端的连接Channel
        if (channel != null) {
            channel.writeAndFlush(gameMessagePackage);//给客户端返回消息
        }
    }



    /*@KafkaListener(topics = {"RaidBattle-Status"}, groupId = "${game.gateway.server.config.server-id}")
    public void raidBattleStatusReceiver(ConsumerRecord<String, byte[]> record) {
        GameMessagePackage gameMessagePackage = GameMessageInnerDecoder.readGameMessagePackageV2(record.value());

        List<?> broadIds = gameMessagePackage.getHeader().getAttribute().getBroadIds();
        List<Long> playerIds = broadIds.stream().map(each -> Long.valueOf(each.toString())).collect(Collectors.toList());
        channelService.broadcast(gameMessagePackage,playerIds);
    }*/

    @KafkaListener(topics = {"RaidBattle-Status"}, groupId = "${game.gateway.server.config.server-id}",containerFactory = "batchContainerFactory")
    public void raidBattleStatusReceiver(List<ConsumerRecord<String, byte[]>> records, Acknowledgment ack) {
        logger.info("Receiver Records:{}",records.size());
        records.forEach(record->{
            String key = record.key();
            Boolean flag = consumeKeys.putIfAbsent(key, true);
            if(flag != null){
                return;
            }
            GameMessagePackage gameMessagePackage = GameMessageInnerDecoder.readGameMessagePackageV2(record.value());
            List<?> broadIds = gameMessagePackage.getHeader().getAttribute().getBroadIds();
            List<Long> playerIds = broadIds.stream().map(each -> Long.valueOf(each.toString())).collect(Collectors.toList());
            channelService.broadcast(gameMessagePackage,playerIds);
        });
        ack.acknowledge();
    }
}
