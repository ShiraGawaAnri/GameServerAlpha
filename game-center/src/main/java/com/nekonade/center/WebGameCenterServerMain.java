package com.nekonade.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WebGameCenterServerMain {

    public static void main(String[] args) {
        SpringApplication.run(WebGameCenterServerMain.class, args);
    }
}
