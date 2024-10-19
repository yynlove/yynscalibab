package com.yyn.mq.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class YynProducer {

    @Autowired
    private YynChannelBinder yynChannelBinder;


    public void sendOneToOneChannelMessage(String message) {
        log.info("YynProducer sendOneToOneChannelMessage {}",message);
        Message<String> build = MessageBuilder.withPayload(message)
                //设置消息延迟级别
                .setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, "2")
                .build();
        yynChannelBinder.sendOneToOneChannel().send(build);
    }


    public void sendOneToManyChannelMessage(String message) {

        log.info("YynProducer sendOneToManyChannelMessage {}",message);
        Message<String> build = MessageBuilder.withPayload(message)
                .build();
        yynChannelBinder.sendOneToManyChannel().send(build);

    }
}
