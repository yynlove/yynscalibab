package com.yyn.yynkafka.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/provider")
//这个注解代表这个类开启Springboot事务，因为我们在Kafka的配置文件开启了Kafka事务，不然会报错
@Transactional(rollbackFor = RuntimeException.class)
public class KafkaProviderController {


    @Resource
    private KafkaTemplate<Object, Object>  kafkaTemplate;

    @GetMapping("/send/{message}")
    public String sendMultiple(@PathVariable("message") String message) {
        ListenableFuture<SendResult<Object, Object>> testTopic = kafkaTemplate.send("test_topic", message);
        return JSON.toJSONString(testTopic);
    }
}
