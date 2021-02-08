package com.nekonade;

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

}
