package com.yyn.yynkafka.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class MyKafkaListenerErrorHandler implements KafkaListenerErrorHandler {

    @Override
    @NonNull
    public Object handleError(@NonNull Message<?> message, @NonNull ListenerExecutionFailedException exception) {
        return new Object();
    }

    @Override
    @NonNull
    public Object handleError(@NonNull Message<?> message, @NonNull ListenerExecutionFailedException exception, Consumer<?, ?> consumer) {
        System.out.println("消息详情：" + message);
        System.out.println("异常信息：：" + exception);
        System.out.println("消费者详情：：" + consumer.groupMetadata());
        System.out.println("监听主题：：" + consumer.listTopics());
        return KafkaListenerErrorHandler.super.handleError(message, exception, consumer);
    }
}
