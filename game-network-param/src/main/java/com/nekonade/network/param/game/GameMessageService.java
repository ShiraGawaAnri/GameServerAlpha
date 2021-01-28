package com.nekonade.network.param.game;

import com.nekonade.network.param.game.common.AbstractGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import com.nekonade.network.param.game.common.IGameMessage;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class GameMessageService {
    private final Logger logger = LoggerFactory.getLogger(GameMessageService.class);
    private final Map<String, Class<? extends IGameMessage>> gameMssageClassMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 初始化的时候，将每个请求的响应的Message的class和messageId对应起来
        Reflections reflections = new Reflections("com.nekonade");
        Set<Class<? extends AbstractGameMessage>> classSet = reflections.getSubTypesOf(AbstractGameMessage.class);
        classSet.forEach(c -> {
            GameMessageMetadata messageMetadata = c.getAnnotation(GameMessageMetadata.class);
            if (messageMetadata != null) {
                this.checkGameMessageMetadata(messageMetadata, c);
                int messageId = messageMetadata.messageId();
                EnumMesasageType mesasageType = messageMetadata.messageType();
                String key = this.getMessageClassCacheKey(mesasageType, messageId);
                gameMssageClassMap.put(key, c);
            }
        });
    }

    private String getMessageClassCacheKey(EnumMesasageType type, int messageId) {
        return messageId + ":" + type.name();
    }
    //获取响应数据包的实例
    public IGameMessage getResponseInstanceByMessageId(int messageId) {
        return this.getMessageInstance(EnumMesasageType.RESPONSE, messageId);
    }
    //获取请求数据包的实例
    public IGameMessage getRequestInstanceByMessageId(int messageId) {
        return this.getMessageInstance(EnumMesasageType.REQUEST, messageId);
    }
    //获取传数据反序列化的对象实例
    public  IGameMessage getMessageInstance(EnumMesasageType messageType,int messageId) {
        String key = this.getMessageClassCacheKey(messageType, messageId);
        Class<? extends IGameMessage> clazz = this.gameMssageClassMap.get(key);
        if (clazz == null) {
            this.throwMetadataException("找不到messageId:" + key + "对应的响应数据对象Class");
        }
        IGameMessage gameMessage = null;
        try {
            //gameMessage = clazz.newInstance();
            gameMessage = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            String msg = "实例化响应参数出现," + "messageId:" + key + ", class:" + clazz.getName();
            logger.error(msg, e);
            this.throwMetadataException(msg);
        }
        return gameMessage;
    }
    //检测数据对象的正确性
    private void checkGameMessageMetadata(GameMessageMetadata messageMetadata, Class<?> c) {
        int messageId = messageMetadata.messageId();
        if (messageId == 0) {
            this.throwMetadataException("messageId未设置:" + c.getName());
        }
        int serviceId = messageMetadata.serviceId();
        if (serviceId == 0) {
            this.throwMetadataException("serviceId未设置：" + c.getName());
        }
        EnumMesasageType mesasageType = messageMetadata.messageType();
        if (mesasageType == null) {
            this.throwMetadataException("messageType未设置:" + c.getName());
        }

    }

    private void throwMetadataException(String msg) {
        throw new IllegalArgumentException(msg);
    }
}
