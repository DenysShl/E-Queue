package com.example.den.equeue;

import com.example.den.equeue.properties.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication
public class EQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(EQueueApplication.class, args);
    }

}
