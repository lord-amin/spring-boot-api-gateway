package com.tiddev.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchangeDecorator;
import reactivefeign.spring.config.EnableReactiveFeignClients;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
@RestController
@SpringBootApplication
@EnableReactiveFeignClients
@EnableDiscoveryClient
//@EnableWebFluxSecurity
public class GatewayApplicationBoot3 {
    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayApplicationBoot3.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(GatewayApplicationBoot3.class, args);
        logConfig(context);
        swagger(context);
    }

    private static void swagger(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        String property = environment.getProperty("server.address");
        String property1 = environment.getProperty("server.servlet.contextPath");
        String base = "http://" + (property == null ? "localhost" : property) + ":" + environment.getProperty("server.port") + (property1 == null ? "" : property1);
        String json = base + environment.getProperty("springdoc.api-docs.path", "/v3/api-docs");
        String ui = base + "/swagger-ui.html";
        LOGGER.info("resource json address is [" + json + "]");
        LOGGER.info("resource ui address is [" + ui + "]");
    }

    private static void logConfig(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof MapPropertySource propertySource1) {
                for (String propertyName : propertySource1.getPropertyNames()) {
                    LOGGER.info("       {}={}", propertyName, environment.getProperty(propertyName));
                }
            }
        }
    }

    //
    @RequestMapping("/fallback")
    public Object fallback(ServerWebExchangeDecorator serverWebExchangeDecorator)  {
//        String uri = serverWebExchangeDecorator.getDelegate().getRequest().getPath().value();
//        throw new GlobalException("service_unavailable", HttpStatus.SERVICE_UNAVAILABLE) {
//            {
//                getMessages().add("Service " + uri + " unavailable");
//            }
//
//        };
        List<URI> originalRequestUris = new ArrayList<>(serverWebExchangeDecorator.getAttributeOrDefault(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR, null));
        try {
            ResponseStatusException throwable = serverWebExchangeDecorator.getAttributeOrDefault(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR, null);
            LOGGER.warn("Type 1 FallBack happened for {}", originalRequestUris.get(0));
            LOGGER.warn("falling back", throwable);
            return ResponseEntity.status(throwable.getStatusCode()).body(throwable.getMessage());
        } catch (Exception e) {
            Throwable throwable = serverWebExchangeDecorator.getAttributeOrDefault(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR, null);
            LOGGER.warn("Type 2 FallBack happened for {},{}", throwable.getClass(), originalRequestUris.get(0));
            LOGGER.warn("falling back", throwable);
            return Mono.error(throwable);
        }
    }
//    @Bean
//    public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
//            ConfigurableApplicationContext context) {
//        return ServiceInstanceListSupplier.builder()
//                .withDiscoveryClient()
//                .withHealthChecks()
//                .build(context);
//    }
}
