package com.yyn.mq;

import com.yyn.mq.stream.YynChannelBinder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;


@EnableFeignClients
@EnableDiscoveryClient
@EnableBinding({YynChannelBinder.class})
@SpringBootApplication()
@MapperScan(value = "com.yyn.mq.mapper")
public class YynMqApplication {

    public static void main(String[] args) {
        SpringApplication.run(YynMqApplication.class, args);
    }


}
