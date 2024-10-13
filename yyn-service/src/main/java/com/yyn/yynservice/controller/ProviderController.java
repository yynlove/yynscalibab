package com.yyn.yynservice.controller;

import cn.hutool.json.JSONUtil;
import com.yyn.yynservice.entity.Table1;
import com.yyn.yynservice.service.Table1Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ProviderController {

    @Value("${server.port}")
    private String port;

    @Resource
    private Table1Service table1Service;

    @GetMapping(value = "/test/{message}")
    public String test(@PathVariable("message") String message) {

        List<Table1> list = table1Service.list();
        return "当前服务收到消息： " + message + "， 该服务由端口： " + port + "提供。" +
                JSONUtil.toJsonStr(list);
    }


}
