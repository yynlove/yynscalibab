package com.yyn.yynweb.controller.test;

import com.yyn.yynweb.feigns.ProviderFeign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test")
public class TestController {
//    @Resource
//    private LoadBalancerClient loadBalancerClient;
//    @Resource
//    private RestTemplate restTemplate;

    @Resource
    private ProviderFeign providerFeign;

    @Value("${spring.application.name}")
    private String appName;

//    @GetMapping(value = "/name")
//    public String test() {
//        //使用 LoadBalanceClient 和 RestTemplate 结合的方式来访问
//        ServiceInstance serviceInstance = loadBalancerClient.choose("yyn-service");
//        String url = String.format("http://%s:%s/yyn/test/%s", serviceInstance.getHost(), serviceInstance.getPort(), appName);
//        return restTemplate.getForObject(url, String.class);
//    }

    @GetMapping(value = "/feign/name")
    public String testfeign() {
        return "feign : " + providerFeign.test(appName);
    }



}
