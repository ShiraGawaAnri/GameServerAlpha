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

import java.util.Properties;


@SpringBootApplication(scanBasePackages = {"com.nekonade"})
public class GameTestAlphaMain {

    public static void main(String args[]) {
        ApplicationContext context = SpringApplication.run(GameTestAlphaMain.class);
        ServerConfig serverConfig = context.getBean(ServerConfig.class);
        DispatchGameMessageService.scanGameMessages(context, serverConfig.getServiceId(), "com.nekonade");

        NamingService naming;

        Properties properties = new Properties();
        properties.setProperty("serverAddr", "127.0.0.1:9090");
        properties.setProperty("namespace", "b8142ed2-6e55-49f3-9e7a-ca83ed3679b6");
        {
            try {
                naming = NamingFactory.createNamingService(properties);
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


}
