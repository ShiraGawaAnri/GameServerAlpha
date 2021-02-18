package com.nekonade.raidbattle.message.context;

import com.nekonade.common.utils.TopicUtil;
import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.network.param.game.common.GameMessagePackage;
import com.nekonade.raidbattle.message.channel.RaidBattleChannelPromise;
import com.nekonade.raidbattle.message.channel.RaidBattleIMessageSendFactory;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

public class RaidBattleGatewayMessageSendFactory implements RaidBattleIMessageSendFactory {
    private final String topic;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public RaidBattleGatewayMessageSendFactory(KafkaTemplate<String, byte[]> kafkaTemplate, String topic) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendMessage(GameMessagePackage gameMessagePackage, RaidBattleChannelPromise promise) {

        int toServerId = gameMessagePackage.getHeader().getToServerId();
        long playerId = gameMessagePackage.getHeader().getPlayerId();
        String raidId = gameMessagePackage.getHeader().getAttribute().getRaidId();
        // 动态创建游戏网关监听消息的topic
        String sendTopic = TopicUtil.generateTopic(topic, toServerId);
        byte[] value = GameMessageInnerDecoder.sendMessage(gameMessagePackage);
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(sendTopic, raidId, value);
        kafkaTemplate.send(record);
        if(promise != null){
            promise.setSuccess();
        }
    }


}
