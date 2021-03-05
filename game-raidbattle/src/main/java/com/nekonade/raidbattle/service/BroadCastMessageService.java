package com.nekonade.raidbattle.service;

import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.common.gameMessage.GameMessageHeader;
import com.nekonade.common.gameMessage.GameMessagePackage;
import com.nekonade.network.param.game.message.battle.RaidBattleAttackMsgResponse;
import com.nekonade.network.param.game.message.battle.RaidBattleBoardCastMsgResponse;
import com.nekonade.raidbattle.message.channel.RaidBattleChannelConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class BroadCastMessageService {

    @Autowired
    private RaidBattleChannelConfig serverConfig;

    @Resource
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    private void broadcast(RaidBattleBoardCastMsgResponse gameMessage, String topic, List<Long> broadIds) {
        GameMessageHeader header = gameMessage.getHeader();
        String raidId = gameMessage.getBodyObj().getRaidId();
        header.getAttribute().setBroadIds(broadIds);
        header.getAttribute().setRaidId(raidId);
        GameMessagePackage gameMessagePackage = new GameMessagePackage();
        gameMessagePackage.setHeader(header);
        gameMessagePackage.setBody(gameMessage.body());
        byte[] value = GameMessageInnerDecoder.sendMessageV2(gameMessagePackage);
        StringBuffer keyId = new StringBuffer();
        keyId.append(header.getPlayerId()).append("_").append(header.getClientSeqId()).append("_").append(header.getClientSendTime());
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, keyId.toString(), value);
        kafkaTemplate.send(record);
    }


    public void broadCastRaidBattleStatus(RaidBattleBoardCastMsgResponse gameMessage, Long[] playerIds) {
        String topic = "RaidBattle-Status";
        List<Long> broadIds = Arrays.asList(playerIds);
        broadcast(gameMessage, topic, broadIds);
    }

    public void broadCastRaidBattleStatus(RaidBattleBoardCastMsgResponse gameMessage, long playerId) {
        String topic = "RaidBattle-Status";
        List<Long> broadIds = Collections.singletonList(playerId);
        broadcast(gameMessage, topic, broadIds);
    }

    public void broadCastRaidBattleStatus(RaidBattleBoardCastMsgResponse gameMessage, List<Long> playerIds) {
        String topic = "RaidBattle-Status";
        broadcast(gameMessage, topic, playerIds);
    }


}
