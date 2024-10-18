package com.yyn.mq.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableBinding(value = YynChannelBinder.class)
public class YynConsumer {

    @Value("${server.port}")
    private String port;

    @StreamListener(YynChannelBinder.ONE_TO_ONE_INPUT)
    public void receive(String messageBody) {
        log.info(port + ">>> Receive1: 通过stream收到消息，messageBody = {}", messageBody);
    }



}
