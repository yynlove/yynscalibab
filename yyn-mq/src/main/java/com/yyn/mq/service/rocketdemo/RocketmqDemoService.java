package com.yyn.mq.service.rocketdemo;

import com.yyn.mq.entity.OrderItem;
import com.yyn.mq.entity.Table1;
import com.yyn.mq.service.Table1Service;
import com.yyn.mq.stream.YynProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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

    @Transactional(rollbackFor = Exception.class)
    public void sendOTOTrans(String message) {
        Table1 table1 = new Table1();
        table1.setTest1(message);
        table1Service.save(table1);
        log.info("--------1 写库");
        yynProducer.sendOTOTrans(table1);

        System.out.println(" sendOTOTrans 消息发送成功");
    }
}
