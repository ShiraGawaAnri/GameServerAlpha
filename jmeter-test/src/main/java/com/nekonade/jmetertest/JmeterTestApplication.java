package com.nekonade.jmetertest;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class JmeterTestApplication {

    public static void main(String[] args) {
        
        SpringApplication.run(JmeterTestApplication.class, args);
        Arguments params = new Arguments();
        JavaSamplerContext arg0 = new JavaSamplerContext(params);
        StressTesting stressTesting = new StressTesting();
        stressTesting.setupTest(arg0);
        stressTesting.runTest(arg0);
    }

}
