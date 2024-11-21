package com.yyn.mq.stream;

import com.alibaba.fastjson.JSON;
import com.yyn.mq.entity.OrderItem;
import com.yyn.mq.entity.Table1;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Component
@Slf4j
public class YynProducer {

    @Autowired
    private YynChannelBinder yynChannelBinder;



    public void sendOneToOneChannelMessage(String message) {
        log.info("YynProducer sendOneToOneChannelMessage {}",message);
        Message<String> build = MessageBuilder.withPayload(message)
                //设置消息延迟级别
//                .setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, "2")
                .build();
        yynChannelBinder.sendOneToOneChannel().send(build);
    }


    public void sendOneToManyChannelMessage(String message) {

        log.info("YynProducer sendOneToManyChannelMessage {}",message);
        Message<String> build = MessageBuilder.withPayload(message)
                .build();
        yynChannelBinder.sendOneToManyChannel().send(build);

    }

    public void sendOneToOneOrderChannelMessage(OrderItem orderItem) {


        Message<OrderItem> build = MessageBuilder.withPayload(orderItem)
//                .setHeader("orderId", orderItem.getId())
//                .setHeader("rocketmq_TAGS", orderItem.getMessage())
                .setHeader(MessageConst.PROPERTY_TAGS, orderItem.getMessage())  //基于标签过滤
                .build();
        log.info("YynProducer sendOneToOneOrderChannelMessage {}", JSON.toJSON(build));
        yynChannelBinder.sendOneToOneOrderChannel().send(build);
    }

    public void sendOTOTrans(Table1 table1) {
        String jsonString = JSON.toJSONString(table1);
        Message<Table1> springMessage = MessageBuilder.withPayload(table1)
                .setHeader("TRANSACTION_ID", table1.getTest4())
                .setHeader("args", jsonString) // <X>
                .build();
        // 发送消息
        log.info("sendOTOTrans {}",table1.getTest4());
        yynChannelBinder.sendOneToOneTransChannel().send(springMessage);

    }
}
