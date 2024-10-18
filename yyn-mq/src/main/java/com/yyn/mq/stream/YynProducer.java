package com.yyn.mq.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@EnableBinding({YynChannelBinder.class})
public class YynProducer {

    @Autowired
    private YynChannelBinder yynChannelBinder;


    public void sendOneToOneChannelMessage(String message) {
        log.info("YynProducer sendOneToOneChannelMessage {}",message);
        yynChannelBinder.sendOneToOneChannel().send(MessageBuilder.withPayload(message).build());
    }


}
