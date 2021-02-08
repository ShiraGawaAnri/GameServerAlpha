package com.nekonade.network.param.game.messagedispatcher;

import com.nekonade.network.param.game.common.GameMessageMetadata;
import com.nekonade.network.param.game.common.IGameMessage;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DispatchGameMessageService {
    private final Logger logger = LoggerFactory.getLogger(DispatchGameMessageService.class);
    private final Map<String, DispatcherMapping> dispatcherMappingMap = new HashMap<>();
    @Autowired
    private ApplicationContext applicationContext;// 注入spring上下文

    /**
     * <p>
     * Description: 服务启动的时候调用此方法，扫描获取此服务要处理的game message类。
     * </p>
     *
     * @param applicationContext
     * @param serviceId          服务id，如果为0,则加载所有的消息类型，如果不为零，则只加载此类型的消息。
     * @param packagePath        消息所在的包路径
     * @author wgs
     * @date 2019年4月27日 下午7:05:11
     */
    public static void scanGameMessages(ApplicationContext applicationContext, int serviceId, String packagePath) {// 构造一个方便的调用方法
        DispatchGameMessageService dispatchGameMessageService = applicationContext.getBean(DispatchGameMessageService.class);
        dispatchGameMessageService.scanGameMessages(serviceId, packagePath);
    }

    public void scanGameMessages(int serviceId, String packagePath) {
        Reflections reflection = new Reflections(packagePath);
        Set<Class<?>> allGameMessageHandlerClass = reflection.getTypesAnnotatedWith(GameMessageHandler.class);// 根据注解，获取所有标记了这个注解的所有类的Class类
        if (allGameMessageHandlerClass != null) {
            allGameMessageHandlerClass.forEach(c -> {// 遍历获得的所有的Class
                Object targetObject = applicationContext.getBean(c);// 根据Class从spring中获取它的实例，从spring中获取实例的好处是，把处理消息的类纳入到spring的管理体系中。
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
        String key = gameMessage.getClass().getName();
        DispatcherMapping dispatcherMapping = this.dispatcherMappingMap.get(key);// 根据消息的ClassName找到调用方法的信息
        if (dispatcherMapping != null) {
            Object obj = dispatcherMapping.getTargetObj();
            try {
                dispatcherMapping.getTargetMethod().invoke(obj, gameMessage, ctx);// 调用处理消息的方法
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("调用方法异常，方法所在类：{}，方法名：{}", obj.getClass().getName(), dispatcherMapping.getTargetMethod().getName(), e);
            }
        } else {
            logger.warn("消息未找到处理的方法，消息名：{}", key);
        }
    }
}
