package com.yyn.yynservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan(basePackages = "com.yyn.yynservice.mapper")
public class YynServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(YynServiceApplication.class, args);
    }

}