package com.yyn.mq.service.rocketdemo;

import com.yyn.mq.entity.OrderItem;
import com.yyn.mq.entity.Table1;
import com.yyn.mq.service.Table1Service;
import com.yyn.mq.stream.YynProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@Service
public class RocketmqDemoService {

    @Resource
    private YynProducer yynProducer;
    @Resource
    private Table1Service table1Service;

    public void sendOTO(String message) {
        yynProducer.sendOneToOneChannelMessage(message);
    }

    public void sendOTM(String message) {
        yynProducer.sendOneToManyChannelMessage(message);
    }

    public void sendOTOOrder(String message) {

        for (int i = 0;i<3;i++){
            OrderItem orderItem = new OrderItem(i, message);
            yynProducer.sendOneToOneOrderChannelMessage(orderItem);
        }

    }

    public void sendOTOTrans(String message) {
        //不做业务处理
        Table1 table1 = new Table1();
        table1.setTest1(message + "开始发消息");
        String transactionId = UUID.randomUUID().toString();
        table1.setTest4(transactionId);
        //发送消息
        yynProducer.sendOTOTrans(table1);
        System.out.println(" sendOTOTrans 消息发送成功");
    }
}
