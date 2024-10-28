package com.yyn.mq.service.rocketdemo;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yyn.mq.entity.Table1;
import com.yyn.mq.service.Table1Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Component
public class OneToOneTransListener implements TransactionListener {

    @Resource
    private Table1Service table1Service;

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        Map<String, String> headers = message.getProperties();
        String transactionId = headers.get("TRANSACTION_ID");
        String s = headers.get("args");
        log.info("executeLocalTransaction transactionId {} args {}",transactionId,s);
        //处理业务
        try {
            table1Service.transSave(s);
            return LocalTransactionState.COMMIT_MESSAGE;
        }catch (Exception e){
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        log.info("LocalTransactionState {}",JSON.toJSON(messageExt));
        Map<String, String> headers = messageExt.getProperties();
        String transactionId = headers.get("TRANSACTION_ID");
        LambdaQueryWrapper<Table1> eq = new QueryWrapper<Table1>().lambda().eq(Table1::getTest4, transactionId);
        int count = this.table1Service.count(eq);
        if(count>0){
            return LocalTransactionState.COMMIT_MESSAGE;
        }

        return LocalTransactionState.ROLLBACK_MESSAGE;
    }
}