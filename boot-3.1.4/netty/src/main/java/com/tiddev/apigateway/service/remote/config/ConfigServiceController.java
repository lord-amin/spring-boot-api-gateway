package com.tiddev.apigateway.service.remote.config;

import com.tiddev.apigateway.config.WebSecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController("/api")
public class ConfigServiceController {
    private final ConfigService configService;
    /**
     * @see WebSecurityConfig#configureResourceServer(ServerHttpSecurity httpSecurity)
     */
    @GetMapping("/protected/test")
    public Mono<Map<String, String>> getConfigFor() {
        log.info("Trying to send ");
        return configService.getConfig();
    }
    /**
     * @see WebSecurityConfig#configureResourceServer(ServerHttpSecurity httpSecurity)
     */
    @GetMapping("/unprotected/test")
    public Mono<Map<String, String>> getConfigForU() {
        log.info("Trying to send ");
        return configService.getConfig();
    }
}
