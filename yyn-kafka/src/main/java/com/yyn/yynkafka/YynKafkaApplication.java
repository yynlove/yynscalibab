package com.yyn.yynkafka;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan(value = "com.yyn.yynkafka.mapper")
public class YynKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(YynKafkaApplication.class, args);
    }

}
