package com.nekonade.common.utils;

import com.nekonade.common.gameMessage.GameMessageHeader;
import com.nekonade.common.gameMessage.GameMessagePackage;
import com.nekonade.common.gameMessage.HeaderAttribute;
import com.nekonade.common.gameMessage.IGameMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageUtils {

    private static Logger logger = LoggerFactory.getLogger(MessageUtils.class);

    public static String kafkaKeyCreate(GameMessageHeader header){
        long playerId = header.getPlayerId();
        int clientSeqId = header.getClientSeqId();
        long clientSendTime = header.getClientSendTime();
        if(playerId == 0 || clientSeqId == 0 || clientSendTime == 0){
            logger.warn("Kafka KeyId数据不精确 PlayerId:{} ClientSeqId:{} ClientSendTime:{}",playerId,clientSeqId,clientSendTime);
        }
        StringBuffer key = new StringBuffer();
        key.append(playerId).append("_").append(header.getClientSeqId()).append("_").append(header.getClientSendTime());
        return key.toString();
    }

    public static <T extends IGameMessage>void CalcMessageDealTime(Logger logger,T gameMessage){
        GameMessageHeader header = gameMessage.getHeader();
        HeaderAttribute attribute = header.getAttribute();
        Long time = Math.abs(header.getServerSendTime() - header.getClientSendTime());
        Long longTime = 3000L;
        if(time == 0 || time >= 1600000000000L){
            logger.debug("MessageId:{} [ServerSend] Player:{} RaidId:{}",header.getMessageId(),header.getPlayerId(),attribute.getRaidId());
        }else if(time >= longTime){
            logger.warn("MessageId:{} Time:{} Player:{} RaidId:{} 处理有较大延迟(>={})",header.getMessageId(),time,header.getPlayerId(),attribute.getRaidId(),longTime);
        }else{
            logger.info("MessageId:{} Time:{} Player:{} RaidId:{}",header.getMessageId(),time,header.getPlayerId(),attribute.getRaidId());
        }
    }

    public static <T extends GameMessagePackage>void CalcMessageDealTime(Logger logger, T gameMessage){
        GameMessageHeader header = gameMessage.getHeader();
        HeaderAttribute attribute = header.getAttribute();
        Long time = Math.abs(header.getServerSendTime() - header.getClientSendTime());
        Long longTime = 3000L;
        if(time == 0 || time >= 1600000000000L){
            logger.debug("MessageId:{} [ServerSend] Player:{} Seq:{} RaidId:{}",header.getMessageId(),header.getPlayerId(),header.getClientSeqId(),attribute.getRaidId());
        }else if(time >= longTime){
            logger.warn("MessageId:{} TotalTime:{} Player:{} Seq:{} RaidId:{} 处理有较大延迟(>={})",header.getMessageId(),time,header.getPlayerId(),header.getClientSeqId(),attribute.getRaidId(),longTime);
        }else{
            logger.info("MessageId:{} TotalTime:{} Player:{} Seq:{} RaidId:{}",header.getMessageId(),time,header.getPlayerId(),header.getClientSeqId(),attribute.getRaidId());
        }
    }

    public static <T extends IGameMessage> void CalcMessageDealTimeNow(Logger logger, T gameMessage){
        GameMessageHeader header = gameMessage.getHeader();
        HeaderAttribute attribute = header.getAttribute();
        Long time = Math.abs(System.currentTimeMillis() - header.getClientSendTime());
        Long longTime = 3000L;
        if(time == 0 || time >= 1600000000000L){
            logger.debug("MessageId:{} [ServerSend] Player:{} RaidId:{}",header.getMessageId(),header.getPlayerId(),attribute.getRaidId());
        }else if(time >= longTime){
            logger.warn("MessageId:{} Time:{} Player:{} RaidId:{} 处理有较大延迟(>={})",header.getMessageId(),time,header.getPlayerId(),attribute.getRaidId(),longTime);
        }else{
            logger.info("MessageId:{} Time:{} Player:{} RaidId:{}",header.getMessageId(),time,header.getPlayerId(),attribute.getRaidId());
        }

    }
}
