package com.yyn.mq.service.rocketdemo;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
@Slf4j
@RocketMQTransactionListener(txProducerGroup = "trans")
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {


    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        // 从消息 Header 中解析到 args 参数，并使用 JSON 反序列化

        // ... local transaction process, return rollback, commit or unknown
        log.info("[executeLocalTransaction][执行本地事务，消息：{} args：{}]", msg);
        return RocketMQLocalTransactionState.UNKNOWN;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        // ... check transaction status and return rollback, commit or unknown
        log.info("[checkLocalTransaction][回查消息：{}]", msg);
        return RocketMQLocalTransactionState.COMMIT;
    }

}