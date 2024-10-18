package com.yyn.mq.controller;

import com.yyn.mq.service.RocketmqDemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/rocketmqDemo")
public class RocketmqDemoController {

    @Resource
    private RocketmqDemoService rocketmqDemoService;
    @GetMapping(value = "/sendOTO/{message}")
    public String sendOTO(@PathVariable("message") String message){
        rocketmqDemoService.sendOTO(message);
        return "sucess";
    }
}
