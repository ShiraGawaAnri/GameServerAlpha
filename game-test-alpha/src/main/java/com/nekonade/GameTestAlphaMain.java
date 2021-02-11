package com.nekonade;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.nekonade.network.message.context.ServerConfig;
import com.nekonade.network.param.game.messagedispatcher.DispatchGameMessageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.nekonade"})
public class GameTestAlphaMain {

    public static void main(String args[]) {
        ApplicationContext context = SpringApplication.run(GameTestAlphaMain.class);
        ServerConfig serverConfig = context.getBean(ServerConfig.class);
        DispatchGameMessageService.scanGameMessages(context, serverConfig.getServiceId(), "com.nekonade");
    }
    NamingService naming;

    {
        try {
            naming = NamingFactory.createNamingService("127.0.0.1:9090");
            naming.subscribe("game-logic","RAID_BATTLE", event -> {
                if (event instanceof NamingEvent) {
                    System.out.println(((NamingEvent) event).getServiceName());
                    System.out.println(((NamingEvent) event).getInstances());
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

}
