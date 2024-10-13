package com.yyn.yynweb.feigns.fallback;

import com.yyn.yynweb.feigns.ProviderFeign;
import org.springframework.stereotype.Component;

@Component
public class ProviderFeignFallback implements ProviderFeign {
    @Override
    public String test(String message) {
        return "熔断测试";
    }
}
