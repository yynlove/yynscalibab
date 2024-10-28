package com.yyn.mq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yyn.mq.entity.Table1;


public interface Table1Service extends IService<Table1> {


    void transSave(String s);
}
