package com.yyn.yynkafka.consumer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class KafkaConsumer {


    /**
     * 监听kafka消息
     *
     * @param consumerRecord kafka的消息，用consumerRecord可以接收到更详细的信息，也可以用String message只接收消息
     * @param ack  kafka的消息确认
     * 使用autoStartup = "false"必须指定id
     */
    @KafkaListener(topics = {"test_topic"},groupId = "yyn_kafka_group_b",containerFactory = "kafkaListenerContainerFactory")
    public void listen1(List<ConsumerRecord<Object, Object>> consumerRecord, Acknowledgment ack) {
        try {
            log.info("KafkaConsumer listen1 {}", consumerRecord.size());
            //用于测试异常处理
//            int i = 1 / 0;
            log.info("{}",consumerRecord.get(0).value());
            //手动确认
            ack.acknowledge();
        } catch (Exception e) {
            log.info("消费失败：" + e);
        }
    }


}
