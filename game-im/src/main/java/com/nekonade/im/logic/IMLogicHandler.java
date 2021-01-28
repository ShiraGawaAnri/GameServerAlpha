package com.nekonade.im.logic;

import com.alibaba.fastjson.JSON;
import com.nekonade.dao.daos.PlayerDao;
import com.nekonade.common.db.entity.manager.IMManager;
import com.nekonade.dao.redis.EnumRedisKey;
import com.nekonade.network.message.context.GatewayMessageConsumerService;
import com.nekonade.network.message.context.GatewayMessageContext;
import com.nekonade.network.param.game.message.im.IMSendIMMsgRequest;
import com.nekonade.network.param.game.message.im.IMSendIMMsgeResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@GameMessageHandler
public class IMLogicHandler {

    private final static Logger logger = LoggerFactory.getLogger(IMLogicHandler.class);

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;
    private final static String IM_TOPIC = "game-im-topic";
    @Autowired
    private GatewayMessageConsumerService gatewayMessageConsumerService;

    @Autowired
    private PlayerDao playerDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //发布消息Kafka服务之中
    private void publishMessage(ChatMessage chatMessage) {
        String json = JSON.toJSONString(chatMessage);
        try {
            byte[] message = json.getBytes("utf8");
            ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(IM_TOPIC, "IM", message);
            kafkaTemplate.send(record);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    //这里需要注意的是groupId一定要不一样，因为kafka的机制是一个消息只能被同一个消费者组下的某个消费者消费一次。不同的服务实例的serverId不一样
    @KafkaListener(topics = {IM_TOPIC},groupId= "IM-SERVER-" + "${game.server.config.server-id}")
    public void messageListener(ConsumerRecord<String, byte[]> record) {
        //监听聊天服务发布的信息，收到信息之后，将聊天信息转发到所有的客户端。
        byte[] value = record.value();
        String json = new String(value, StandardCharsets.UTF_8);
        ChatMessage chatMessage = JSON.parseObject(json, ChatMessage.class);
        IMSendIMMsgeResponse response = new IMSendIMMsgeResponse();
        response.getBodyObj().setChat(chatMessage.getChatMessage());
        response.getBodyObj().setSender(chatMessage.getNickName());
        //因为这里不再GatewayMessageContext参数，所以这里使用总的GameChannel管理类，将消息广播出去
        gatewayMessageConsumerService.getGameMessageEventDispatchService().broadcastMessage(response);
    }
    @GameMessageMapping(IMSendIMMsgRequest.class)//在这里接收客户端发送的聊天消息
    public void chatMsg(IMSendIMMsgRequest request, GatewayMessageContext<IMManager> ctx) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatMessage(request.getBodyObj().getChat());
        //chatMessage.setNickName(ctx.getPlayer().getNickName());
        long playerId = ctx.getPlayerId();
        String key = EnumRedisKey.PLAYERID_TO_PLAYER_NICKNAME.getKey(String.valueOf(playerId));
        String nickname = redisTemplate.opsForValue().get(key);
        if(StringUtils.isEmpty(nickname)){
            logger.warn("来源不明的消息{}",chatMessage);
            return;
        }
        chatMessage.setNickName(nickname);
        chatMessage.setPlayerId(playerId);
        logger.info("IM服务器收到消息{}",chatMessage);
        this.publishMessage(chatMessage);//收到客户端的聊天消息之后，把消息封装，发布到Kafka之中。
    }


}
