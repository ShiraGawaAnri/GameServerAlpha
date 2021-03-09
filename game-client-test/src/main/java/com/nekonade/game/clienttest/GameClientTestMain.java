package com.nekonade.game.clienttest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.nekonade.game.clienttest", "com.nekonade.network.param"},exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class GameClientTestMain {
    public static void main(String[] args) {
        SpringApplication.run(GameClientTestMain.class,args);
        //app.setWebApplicationType(WebApplicationType.NONE);// 客户端不需要是一个web服务

    }
}
