package com.nekonade.log.GameLog.service;

import com.nekonade.common.gameMessage.IGameMessage;
import com.nekonade.dao.daos.AsyncLogDao;
import com.nekonade.dao.db.entity.LogGameLogic;
import com.nekonade.log.GameLog.config.ServerConfig;
import com.nekonade.network.param.game.GameMessageService;
import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.common.gameMessage.GameMessageHeader;
import com.nekonade.common.gameMessage.GameMessagePackage;
import com.nekonade.network.param.log.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ReceiveGameLogicLog {

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private GameMessageService gameMessageService;

    @Autowired
    private AsyncLogDao asyncLogDao;

    private static final Logger logger = LoggerFactory.getLogger(ReceiveGameLogicLog.class);

    @PostConstruct
    public void init() {
        logger.info("监听消息接收业务消息topic:{}", serverConfig.toString());
    }

    @KafkaListener(topics = {"${log.server.config.game-logic}"}, groupId = "${log.server.config.topic-group-id}")
    public void GameLogicLogReceiver(ConsumerRecord<byte[], byte[]> record) {
        GameMessagePackage gameMessagePackage = GameMessageInnerDecoder.readGameMessagePackageV2(record.value());
        GameMessageHeader header = gameMessagePackage.getHeader();
        IGameMessage targetClass = gameMessageService.getRequestInstanceByMessageId(header.getMessageId());
        byte[] key = record.key();
        logger.info("Record Key {}",new String(key));
        LogTable logTable = LogTable.readBody(gameMessagePackage.getBody());
        LogGameLogic logGameLogic = new LogGameLogic();
        BeanUtils.copyProperties(logTable,logGameLogic);

        byte[] gameMessage = logTable.getGameMessage();
        targetClass.read(gameMessage);
        logGameLogic.setGameMessage(targetClass);

        asyncLogDao.saveGameLogicLog(logGameLogic);
    }
}
