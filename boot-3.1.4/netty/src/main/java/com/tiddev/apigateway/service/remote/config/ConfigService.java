package com.tiddev.apigateway.service.remote.config;

import org.springframework.web.bind.annotation.GetMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Yaser(amin) Sadeghi
 */
//@Component
@ReactiveFeignClient("config-service")
//@Import(FeignAutoConfiguration.class)
public interface ConfigService {

    @GetMapping(path = "/config-service/test")
    Mono<Map<String, String>> getConfig();
}
