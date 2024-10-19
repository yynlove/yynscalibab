package com.yyn.mq.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import java.net.DatagramSocket;

@Component
public interface YynChannelBinder {


    String ONE_TO_ONE_OUTPUT = "one-to-one-output";
    String ONE_TO_ONE_INPUT = "one-to-one-input";

    String ONE_TO_MANY_OUTPUT = "one-to-many-output";
    String ONE_TO_MANY_INPUT = "one-to-many-input";

    @Output(ONE_TO_ONE_OUTPUT)
    MessageChannel sendOneToOneChannel();
    @Input(ONE_TO_ONE_INPUT)
    MessageChannel  receiveOneToOne();


    @Output(ONE_TO_MANY_OUTPUT)
    MessageChannel sendOneToManyChannel();
    @Input(ONE_TO_MANY_INPUT)
    MessageChannel  receiveOneToMANY();
}
