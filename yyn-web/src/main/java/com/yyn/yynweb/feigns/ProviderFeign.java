package com.yyn.yynweb.feigns;

import com.yyn.yynweb.feigns.fallback.ProviderFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "yyn-service",path = "/yynservice",fallback = ProviderFeignFallback.class)
public interface ProviderFeign {

     @GetMapping(value = "/test/{message}")
     String test(@PathVariable("message") String message);

}
