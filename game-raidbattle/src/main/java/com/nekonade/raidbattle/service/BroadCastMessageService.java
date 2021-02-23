package com.nekonade.raidbattle.service;

import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.network.param.game.common.GameMessageHeader;
import com.nekonade.network.param.game.common.GameMessagePackage;
import com.nekonade.network.param.game.message.battle.RaidBattleAttackMsgResponse;
import com.nekonade.raidbattle.message.channel.RaidBattleChannelConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class BroadCastMessageService {

    @Autowired
    private RaidBattleChannelConfig serverConfig;

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    private void broadcast(RaidBattleAttackMsgResponse gameMessage, String topic, List<Long> broadIds) {
        GameMessageHeader header = gameMessage.getHeader();
        String raidId = gameMessage.getBodyObj().getRaidId();
        header.getAttribute().setBroadIds(broadIds);
        header.getAttribute().setRaidId(raidId);
        GameMessagePackage gameMessagePackage = new GameMessagePackage();
        gameMessagePackage.setHeader(header);
        gameMessagePackage.setBody(gameMessage.body());
        byte[] value = GameMessageInnerDecoder.sendMessageV2(gameMessagePackage);
        ProducerRecord<String, byte[]> responseRecord = new ProducerRecord<>(topic, header.getAttribute().getRaidId(), value);
        kafkaTemplate.send(responseRecord);
    }


    public void broadCastRaidBattleStatus(RaidBattleAttackMsgResponse gameMessage, Long[] playerIds) {
        String topic = "RaidBattle-Status";
        List<Long> broadIds = Arrays.asList(playerIds);
        broadcast(gameMessage, topic, broadIds);
    }

    public void broadCastRaidBattleStatus(RaidBattleAttackMsgResponse gameMessage, long playerId) {
        String topic = "RaidBattle-Status";
        List<Long> broadIds = Collections.singletonList(playerId);
        broadcast(gameMessage, topic, broadIds);
    }

    public void broadCastRaidBattleStatus(RaidBattleAttackMsgResponse gameMessage, List<Long> playerIds) {
        String topic = "RaidBattle-Status";
        broadcast(gameMessage, topic, playerIds);
    }

    public void returnResponseToGateway(){

    }


}
