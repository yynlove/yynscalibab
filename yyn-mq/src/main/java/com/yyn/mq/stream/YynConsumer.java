package com.yyn.mq.stream;

import com.yyn.mq.entity.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableBinding(value = YynChannelBinder.class)
public class YynConsumer {

    @Value("${server.port}")
    private String port;

    @StreamListener(YynChannelBinder.ONE_TO_ONE_INPUT)
    public void receive(String messageBody) {
//        throw new RuntimeException("我就是故意抛出一个异常");
        log.info(port + ">>> Receive1: 通过stream收到消息，messageBody = {}", messageBody);
    }


    @StreamListener(YynChannelBinder.ONE_TO_MANY_INPUT)
    public void receiveOTM(String messageBody) {
        log.info(port + "   " +YynChannelBinder.ONE_TO_MANY_INPUT + " >>> Receive1:，messageBody = {}", messageBody);
    }


    @StreamListener(YynChannelBinder.ONE_TO_ONE_ORDER_INPUT)
    public void receiveOTOOrder(String messageBody) {
        log.info("[ONE_TO_ONE_ORDER_INPUT][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), messageBody);
    }



    @ServiceActivator(inputChannel = "one-to-one-order.one-to-one-order-cus-group.errors")
    public void handleError(ErrorMessage errorMessage) {
        log.error("[handleError][payload：{}]", ExceptionUtils.getRootCauseMessage(errorMessage.getPayload()));
        log.error("[handleError][originalMessage：{}]", errorMessage.getOriginalMessage());
        log.error("[handleError][headers：{}]", errorMessage.getHeaders());
    }

    @StreamListener(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME) // errorChannel
    public void globalHandleError(ErrorMessage errorMessage) {
        log.error("[globalHandleError][payload：{}]", ExceptionUtils.getRootCauseMessage(errorMessage.getPayload()));
        log.error("[globalHandleError][originalMessage：{}]", errorMessage.getOriginalMessage());
        log.error("[globalHandleError][headers：{}]", errorMessage.getHeaders());
    }



}
