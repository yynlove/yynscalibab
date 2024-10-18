package com.yyn.mq.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public interface YynChannelBinder {


    String ONE_TO_ONE_OUTPUT = "one-to-one-output";

    String ONE_TO_ONE_INPUT = "one-to-one-input";

    @Output(ONE_TO_ONE_OUTPUT)
    MessageChannel sendOneToOneChannel();


    @Input(ONE_TO_ONE_INPUT)
    MessageChannel  receiveOneToOne();


}
