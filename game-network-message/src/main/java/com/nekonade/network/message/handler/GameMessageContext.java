package com.xinyue.network.message.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import com.xinyue.network.message.game.IGameMessage;


public class GameMessageContext {
    private Logger logger = LoggerFactory.getLogger(GameMessageContext.class);
    private Map<String, HandlerMethod> dispatcherMappingMap = new HashMap<>();
    private ApplicationContext applicationContext;// 注入spring上下文
    public void init(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.scanGameMessages();
    }
    public void scanGameMessages() {
        //从spring的bean管理器中获取所有的处理请求的Handler类实例
        Map<String, Object> allGameMessageHandlerClass = applicationContext.getBeansWithAnnotation(GameMessageHandler.class);// 根据注解，获取所有标记了这个注解的所有类的Class类
        if (allGameMessageHandlerClass != null && allGameMessageHandlerClass.size() > 0) {
            allGameMessageHandlerClass.values().forEach(targetObject -> {// 遍历获得的所有的bean实例
                Class<?> c = targetObject.getClass();
                Method[] methods = c.getMethods();
                for (Method m : methods) {// 遍历这个类上面的所有方法
                    GameRequestMapping gameMessageMapping = m.getAnnotation(GameRequestMapping.class);
                    if (gameMessageMapping != null) {// 判断此方法上面是否有GameRequestMapping
                        Class<?> gameMessageClass = gameMessageMapping.value();// 从注解中获取处理的IGameMessage对象的Class
                        HandlerMethod dispatcherMapping = new HandlerMethod(targetObject, m);
                        this.dispatcherMappingMap.put(gameMessageClass.getName(), dispatcherMapping);
                        logger.debug("加载MessageHandler类：{},处理消息：{}的方法",c.getName(),m.getName());
                    }
                }
            });
        }
    }
    public HandlerMethod getHandlerMethod(IGameMessage gameMessage) {
        return dispatcherMappingMap.get(gameMessage.getClass().getName());
    }
}
