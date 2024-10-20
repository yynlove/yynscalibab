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


    @GetMapping(value = "/sendOTM/{message}")
    public String sendOTM(@PathVariable("message") String message){
        rocketmqDemoService.sendOTM(message);
        return "one to many sucess";
    }


    @GetMapping(value = "/sendOTOOrder/{message}")
    public String sendOTOOrder(@PathVariable("message") String message){
        rocketmqDemoService.sendOTOOrder(message);
        return "one to one order sucess";
    }

}
