package com.xinyue.network.message.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Service;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Service //让此注解继承@Service注解，在项目启动时，自动扫描被GameMessageHandler注解的类
public @interface GameMessageHandler {

}
