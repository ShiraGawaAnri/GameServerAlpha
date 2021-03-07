package com.nekonade.jmetertest.service;

import com.nekonade.common.gameMessage.GameMessageHeader;
import com.nekonade.common.gameMessage.GameMessageMetadata;
import com.nekonade.common.gameMessage.GameMessagePackage;
import com.nekonade.common.gameMessage.IGameMessage;
import com.nekonade.common.utils.JacksonUtils;
import com.nekonade.network.param.game.GameMessageService;
import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.network.param.game.messagedispatcher.*;
import com.nekonade.network.param.log.LogTable;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestDispatchGameMessageService {

    private final Logger logger = LoggerFactory.getLogger(TestDispatchGameMessageService.class);
    private final Map<String, DispatcherMapping> dispatcherMappingMap = new HashMap<>();

    private final GameMessageService gameMessageService;

    public TestDispatchGameMessageService(GameMessageService gameMessageService) {
        this.gameMessageService = gameMessageService;
    }


    public void scanGameMessages(int serviceId, String packagePath) {
        Reflections reflection = new Reflections(packagePath);
        Set<Class<?>> allGameMessageHandlerClass = reflection.getTypesAnnotatedWith(GameMessageHandler.class);// 根据注解，获取所有标记了这个注解的所有类的Class类
        if (allGameMessageHandlerClass != null) {
            allGameMessageHandlerClass.forEach(c -> {// 遍历获得的所有的Class
                Object targetObject = null;// 根据Class从spring中获取它的实例，从spring中获取实例的好处是，把处理消息的类纳入到spring的管理体系中。
                try {
                    targetObject = c.getDeclaredConstructor().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
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
        logTable.setOperatorId(String.valueOf(header.getPlayerId()));
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
    }
}