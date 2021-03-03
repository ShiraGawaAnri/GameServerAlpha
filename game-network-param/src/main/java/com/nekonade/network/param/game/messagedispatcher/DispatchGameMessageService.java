package com.nekonade.network.param.game.messagedispatcher;

import com.nekonade.common.utils.JacksonUtils;
import com.nekonade.network.param.game.GameMessageService;
import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.common.gameMessage.GameMessageHeader;
import com.nekonade.common.gameMessage.GameMessageMetadata;
import com.nekonade.common.gameMessage.GameMessagePackage;
import com.nekonade.common.gameMessage.IGameMessage;
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

    @Autowired
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
        LocalDateTime now = LocalDateTime.now();
        String key = gameMessage.getClass().getName();
        DispatcherMapping dispatcherMapping = this.dispatcherMappingMap.get(key);// 根据消息的ClassName找到调用方法的信息
        LogTable logTable = new LogTable();
        logTable.setOperatorId(String.valueOf(header.getPlayerId()));
        logTable.setOperateDate(now.toString());
        logTable.setGameMessage(gameMessage.body());
        if (dispatcherMapping != null) {
            Object obj = dispatcherMapping.getTargetObj();
            Method targetMethod = dispatcherMapping.getTargetMethod();
            try {
                logTable.setOperateMethodName(targetMethod.getName());
                targetMethod.invoke(obj, gameMessage, ctx);// 调用处理消息的方法
                logTable.setOperateSuccessful(true);
                logTable.setOperateResult(obj.toString());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("调用方法异常，方法所在类：{}，方法名：{}", obj.getClass().getName(), targetMethod.getName(), e);
            }
        } else {
            logger.warn("消息未找到处理的方法，消息名：{}", key);
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
        keyId.append(header.getPlayerId()).append("_").append(header.getClientSeqId());
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, keyId.toString(), value);
        kafkaTemplate.send(record);
    }
}
