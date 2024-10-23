package com.yyn.mq;

import com.yyn.mq.stream.YynChannelBinder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;


@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan(value = "com.yyn.mq.mapper")
@EnableBinding({YynChannelBinder.class})
public class YynMqApplication {

    public static void main(String[] args) {
        SpringApplication.run(YynMqApplication.class, args);
    }

}
