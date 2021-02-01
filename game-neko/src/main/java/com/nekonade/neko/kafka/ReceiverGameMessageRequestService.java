package com.nekonade.neko.kafka;

import com.nekonade.common.utils.TopicUtil;
import com.nekonade.network.message.context.ServerConfig;
import com.nekonade.network.param.game.GameMessageService;
import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.network.param.game.common.GameMessageHeader;
import com.nekonade.network.param.game.common.GameMessagePackage;
import com.nekonade.network.param.game.common.IGameMessage;
import com.nekonade.network.param.game.message.neko.EnterGameMsgRequest;
import com.nekonade.network.param.game.message.neko.EnterGameMsgResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;


//@Service  //测试的情况下打开
public class ReceiverGameMessageRequestService {
    private static final Logger logger = LoggerFactory.getLogger(ReceiverGameMessageRequestService.class);
    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private GameMessageService gameMessageService;
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;
    @KafkaListener(topics = {"${game.server.config.business-game-message-topic}"}, groupId = "${game.server.config.server-id}")
    public void consume(ConsumerRecord<String, byte[]> record) {
        GameMessagePackage gameMessagePackage = GameMessageInnerDecoder.readGameMessagePackage(record.value());
        logger.debug("接收收网关消息：{}",gameMessagePackage.getHeader());
        GameMessageHeader header = gameMessagePackage.getHeader();
        if(serverConfig.getServerId() == header.getToServerId()) {
        	//如果此条消息的目标是这台服务器，则处理这条消息
        	IGameMessage gameMessage = gameMessageService.getRequestInstanceByMessageId(header.getMessageId());
        	if(gameMessage instanceof EnterGameMsgRequest) {
        		EnterGameMsgResponse response = new EnterGameMsgResponse();//给客户端返回消息，测试
        		GameMessageHeader responseHeader = this.createResponseGameMessageHeader(header);
        		response.setHeader(responseHeader);
        		response.getBodyObj().setNickname("天地无极");
        		response.getBodyObj().setPlayerId(header.getPlayerId());
        		GameMessagePackage gameMessagePackage2 = new GameMessagePackage();
        		gameMessagePackage2.setHeader(responseHeader);
        		gameMessagePackage2.setBody(response.body());
        		//动态创建游戏网关监听消息的topic
        		String topic = TopicUtil.generateTopic(serverConfig.getGatewayGameMessageTopic(), header.getFromServerId());
        		byte[] value = GameMessageInnerDecoder.sendMessage(gameMessagePackage2);
        		ProducerRecord<String, byte[]> responseRecord = new ProducerRecord<String, byte[]>(topic, String.valueOf(header.getPlayerId()), value);
        		kafkaTemplate.send(responseRecord);
        	}
        }
    }
    /**
     * 根据请求的包头，创建响应的包头
     * @param requestGameMessageHeader
     * @return
     */
    private GameMessageHeader createResponseGameMessageHeader(GameMessageHeader requestGameMessageHeader) {
    	GameMessageHeader newHeader = new GameMessageHeader();
    	newHeader.setClientSendTime(requestGameMessageHeader.getClientSendTime());
    	newHeader.setClientSeqId(requestGameMessageHeader.getClientSeqId());
    	newHeader.setFromServerId(requestGameMessageHeader.getToServerId());
    	newHeader.setMessageId(requestGameMessageHeader.getMessageId());
    	newHeader.setPlayerId(requestGameMessageHeader.getPlayerId());
    	newHeader.setServerSendTime(System.currentTimeMillis());
    	newHeader.setServiceId(requestGameMessageHeader.getServiceId());
    	newHeader.setToServerId(requestGameMessageHeader.getFromServerId());
    	newHeader.setVersion(requestGameMessageHeader.getVersion());
    	return newHeader;
    }
}
