package com.tiddev.apigateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@Configuration
public class WebSecurityConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Value("${server.port}")
    private int port;

    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            LOGGER.warn("The request for {} -> {}", exchange.getRequest().getRemoteAddress(), exchange.getRequest().getURI());
            if (!HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
                return chain.filter(exchange);
            }
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            LOGGER.warn("The request for {}:{} with method {}", port, request.getURI(), request.getMethod());
            prepareCorsHeaders(response);
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        };
    }

    private void prepareCorsHeaders(ServerHttpResponse response) {
        LOGGER.warn("Options request from ");
        HttpHeaders headers = response.getHeaders();
        headers.setAccessControlAllowOrigin("*");
        headers.setAccessControlAllowMethods(Arrays.asList(HttpMethod.values()));
        headers.setAccessControlMaxAge(3600);
        headers.setAccessControlAllowHeaders(Collections.singletonList("*"));
    }

    @Bean
    public RegistryEventConsumer<CircuitBreaker> myCircuitBreakerRegistryEventConsumer() {
        return new RegistryEventConsumer<>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
                entryAddedEvent.getAddedEntry().getEventPublisher().onStateTransition(event -> LOGGER.info("ADDED type {} ==> {}", entryAddedEvent.getEventType(), event));
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {
                entryRemoveEvent.getRemovedEntry().getEventPublisher().onStateTransition(event -> LOGGER.info("REMOVED type {} ==> {}", entryRemoveEvent.getEventType(), event));
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
                entryReplacedEvent.getOldEntry().getEventPublisher().onStateTransition(event -> LOGGER.info(" REPLACE OLD type {} ==> {}", entryReplacedEvent.getEventType(), event));
                entryReplacedEvent.getNewEntry().getEventPublisher().onStateTransition(event -> LOGGER.info(" REPLACE NEW type {} ==> {}", entryReplacedEvent.getEventType(), event));
            }
        };
    }

    @Bean
    public SecurityWebFilterChain configureResourceServer(ServerHttpSecurity httpSecurity, AppConfig config) throws Exception {

        return httpSecurity
                .authorizeExchange(authorizeExchangeSpec -> {
                    if (Objects.nonNull(config.getSecurity()) && Objects.nonNull(config.getSecurity().getIgnorePathList())
                            && config.getSecurity().getIgnorePathList().length > 0) {
                        authorizeExchangeSpec
                                .pathMatchers(config.getSecurity().getIgnorePathList()).permitAll().anyExchange().authenticated();
                    } else {
                        authorizeExchangeSpec.anyExchange().authenticated();
                    }
                }).oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(jwtSpec -> {
                })).build();
    }
//    @Bean
//    ReactiveJwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
//        NimbusReactiveJwtDecoder.JwkSetUriReactiveJwtDecoderBuilder builder = NimbusReactiveJwtDecoder
//                .withJwkSetUri(properties.getJwt().getJwkSetUri())
//                .jwsAlgorithms(signatureAlgorithms -> jwsAlgorithms(properties,signatureAlgorithms));
////        customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
//        NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder = builder.build();
////        String issuerUri = this.properties.getIssuerUri();
////        Supplier<OAuth2TokenValidator<Jwt>> defaultValidator = (issuerUri != null)
////                ? () -> JwtValidators.createDefaultWithIssuer(issuerUri) : JwtValidators::createDefault;
//        nimbusReactiveJwtDecoder.setJwtValidator(JwtValidators.createDefault());
//        return nimbusReactiveJwtDecoder;
//    }
//    private void jwsAlgorithms(OAuth2ResourceServerProperties properties,Set<SignatureAlgorithm> signatureAlgorithms) {
//        for (String algorithm : properties.getJwt().getJwsAlgorithms()) {
//            signatureAlgorithms.add(SignatureAlgorithm.from(algorithm));
//        }
//    }
}
