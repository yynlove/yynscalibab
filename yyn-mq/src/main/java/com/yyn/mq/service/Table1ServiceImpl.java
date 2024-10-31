package com.yyn.mq.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yyn.mq.entity.Table1;
import com.yyn.mq.mapper.Table1Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Table1ServiceImpl extends ServiceImpl<Table1Mapper, Table1>  implements Table1Service {


    @Override
    @Transactional
    public void transSave(String s) {
        Table1 table1 = JSON.parseObject(s, Table1.class);
        table1.setTest2("处理业务逻辑");
        this.save(table1);
//        int i = 1 % 0;
    }
}
