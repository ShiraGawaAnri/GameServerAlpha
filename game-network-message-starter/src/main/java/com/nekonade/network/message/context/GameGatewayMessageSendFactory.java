package com.nekonade.network.message.context;

import com.nekonade.common.utils.TopicUtil;
import com.nekonade.network.message.channel.GameChannelPromise;
import com.nekonade.network.message.channel.IMessageSendFactory;
import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.network.param.game.common.GameMessagePackage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

public class GameGatewayMessageSendFactory implements IMessageSendFactory {
    private final String topic;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public GameGatewayMessageSendFactory(KafkaTemplate<String, byte[]> kafkaTemplate, String topic) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendMessage(GameMessagePackage gameMessagePackage, GameChannelPromise promise) {
        
        int toServerId = gameMessagePackage.getHeader().getToServerId();
        long playerId = gameMessagePackage.getHeader().getPlayerId();
        // 动态创建游戏网关监听消息的topic
        String sendTopic = TopicUtil.generateTopic(topic,toServerId);
        byte[] value = GameMessageInnerDecoder.sendMessage(gameMessagePackage);
        ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(sendTopic, String.valueOf(playerId), value);
        kafkaTemplate.send(record);
        promise.setSuccess();
    }


}
