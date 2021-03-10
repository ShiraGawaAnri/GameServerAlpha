package com.nekonade.network.param.game.messagedispatcher;

import com.nekonade.common.gameMessage.*;
import com.nekonade.common.utils.JacksonUtils;
import com.nekonade.network.param.game.GameMessageService;
import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.network.param.log.LogTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DispatchGameMessageService {

    private final Logger logger = LoggerFactory.getLogger(DispatchGameMessageService.class);
    private final Map<String, DispatcherMapping> dispatcherMappingMap = new HashMap<>();
    @Autowired
    private ApplicationContext context;// 注入spring上下文

    @Autowired
    private GameMessageService gameMessageService;

    @Resource(name = "CustomKafkaTemplate")
    private KafkaTemplate<String,byte[]> kafkaTemplate;

    @Autowired
    private LogServerConfig logServerConfig;


    public static void scanGameMessages(ApplicationContext applicationContext, int serviceId, String packagePath) {// 构造一个方便的调用方法
        DispatchGameMessageService dispatchGameMessageService = applicationContext.getBean(DispatchGameMessageService.class);
        dispatchGameMessageService.scanGameMessages(serviceId, packagePath,applicationContext);

    }

    public void scanGameMessages(int serviceId, String packagePath,ApplicationContext applicationContext) {
        Reflections reflection = new Reflections(packagePath);
        Set<Class<?>> allGameMessageHandlerClass = reflection.getTypesAnnotatedWith(GameMessageHandler.class);// 根据注解，获取所有标记了这个注解的所有类的Class类
        if (allGameMessageHandlerClass != null) {
            allGameMessageHandlerClass.forEach(c -> {// 遍历获得的所有的Class
                Object targetObject = context.getBean(c);// 根据Class从spring中获取它的实例，从spring中获取实例的好处是，把处理消息的类纳入到spring的管理体系中。
                Method[] methods = c.getMethods();
                for (Method m : methods) {// 遍历这个类上面的所有方法
                    GameMessageMapping gameMessageMapping = m.getAnnotation(GameMessageMapping.class);
                    if (gameMessageMapping != null) {// 判断此方法上面是否有GameMessageMapping
                        Class<?> gameMessageClass = gameMessageMapping.value();// 从注解中获取处理的IGameMessage对象的Class
                        GameMessageMetadata gameMessageMetadata = gameMessageClass.getAnnotation(GameMessageMetadata.class);
                        if (serviceId == 0 || gameMessageMetadata.serviceId() == serviceId) {// 每个服务只加载自己可以处理的消息类型,如果为0则加载所有的类型
                            DispatcherMapping dispatcherMapping = new DispatcherMapping(targetObject, m);
                            this.dispatcherMappingMap.put(gameMessageClass.getName(), dispatcherMapping);
                        }
                    }
                }
            });
        }
    }

    public void callMethod(IGameMessage gameMessage, IGameChannelContext ctx) {// 当收到网络消息之后，调用此方法。
        GameMessageHeader header = gameMessage.getHeader();
        String key = gameMessage.getClass().getName();
        DispatcherMapping dispatcherMapping = this.dispatcherMappingMap.get(key);// 根据消息的ClassName找到调用方法的信息
        LogTable logTable = new LogTable();
        long playerId = header.getPlayerId();
        logTable.setOperatorId(String.valueOf(playerId));
        //logTable.setOperateDate(now.toString());
        logTable.setOperateTimestamp(System.currentTimeMillis());
        logTable.setGameMessage(gameMessage.body());
        if (dispatcherMapping != null) {
            Object obj = dispatcherMapping.getTargetObj();
            Method targetMethod = dispatcherMapping.getTargetMethod();
            try {
                logTable.setOperateClassName(obj.getClass().getName());
                logTable.setOperateMethodName(targetMethod.getName());
                targetMethod.invoke(obj, gameMessage, ctx);// 调用处理消息的方法
                logTable.setOperateSuccessful(true);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("调用方法异常，方法所在类：{}，方法名：{}", obj.getClass().getName(), targetMethod.getName(), e);
            }
        } else {
            logTable.setRemark(key);
            logger.warn("消息未找到处理的方法，消息名：{}", key);
        }
        //TODO:返回错误提醒给客户端
        logTable.setOperateFinishTimestamp(System.currentTimeMillis());

        Long operateTimestamp = logTable.getOperateTimestamp();
        Long operateFinishTimestamp = logTable.getOperateFinishTimestamp();
        Long dealTime = operateFinishTimestamp - operateTimestamp;
        int messageId = header.getMessageId();
        String raidId = header.getAttribute().getRaidId();
        int clientSeqId = header.getClientSeqId();
        int inWhichGroup = gameMessageService.inWhichGroup(EnumMessageType.REQUEST, messageId);
        String whoami = logServerConfig.getWhoAmI();
        if(dealTime >0 && dealTime <= 1600000000L){
            switch (inWhichGroup){
                default:
                    logger.info("{} MessageId:{} DealTime:{} Player:{} Seq:{}",whoami,messageId,dealTime,playerId,clientSeqId);
                    break;
                case 2:
                    logger.info("{} MessageId:{} DealTime:{} Player:{} Seq:{} RaidId:{}",whoami,messageId,dealTime,playerId,clientSeqId,raidId);
                    break;
            }
        }
        String topic = logServerConfig.getLogGameMessageTopic();
        if(StringUtils.isEmpty(topic)){
            return;
        }
        GameMessagePackage gameMessagePackage = new GameMessagePackage();
        gameMessagePackage.setHeader(header);
        String json = JacksonUtils.toJSONStringV2(logTable);
        gameMessagePackage.setBody(json.getBytes());
        byte[] value = GameMessageInnerDecoder.sendMessageV2(gameMessagePackage);
        StringBuffer keyId = new StringBuffer();
        keyId.append(playerId).append("_").append(clientSeqId).append("_").append(header.getClientSendTime());
        if(header.getClientSendTime() == 0L){
            logger.warn("MessageId:{} ClientSendTime 未正确记录",messageId);
        }
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, keyId.toString(), value);
        kafkaTemplate.send(record);
    }
}
