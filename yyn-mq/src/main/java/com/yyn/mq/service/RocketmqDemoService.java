package com.yyn.mq.service;

import com.yyn.mq.entity.OrderItem;
import com.yyn.mq.stream.YynProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class RocketmqDemoService {

    @Resource
    private YynProducer yynProducer;

    public void sendOTO(String message) {
        yynProducer.sendOneToOneChannelMessage(message);
    }

    public void sendOTM(String message) {
        yynProducer.sendOneToManyChannelMessage(message);
    }

    public void sendOTOOrder(String message) {

        for (int i = 0;i<3;i++){
            OrderItem orderItem = new OrderItem(i, message + 1);
            yynProducer.sendOneToOneOrderChannelMessage(orderItem);
        }

    }
}
