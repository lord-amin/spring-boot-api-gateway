package com.tiddev.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

@Configuration
public class WebSecurityConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, AppConfig config) {
        ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchange = http.cors().and().csrf().disable().authorizeExchange();
        if (Objects.nonNull(config.getSecurity()) && Objects.nonNull(config.getSecurity().getIgnorePathList()) && config.getSecurity().getIgnorePathList().length > 0) {
            Arrays.stream(config.getSecurity().getIgnorePathList()).forEach(s -> LOGGER.warn("The unsecure resource is        {}", s));
            LOGGER.warn("{}", System.lineSeparator());
            authorizeExchange.pathMatchers(config.getSecurity().getIgnorePathList()).permitAll();
        }
        authorizeExchange
                .anyExchange()
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }

//    @Bean
//    ReactiveJwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
//        NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder = NimbusReactiveJwtDecoder.withJwkSetUri(properties.getJwt().getJwkSetUri()).jwsAlgorithm(SignatureAlgorithm.from(properties.getJwt().getJwsAlgorithm())).build();
//        String issuerUri = properties.getJwt().getIssuerUri();
//        Supplier<OAuth2TokenValidator<Jwt>> defaultValidator = issuerUri != null ? () -> {
//            return JwtValidators.createDefaultWithIssuer(issuerUri);
//        } : JwtValidators::createDefault;
//        nimbusReactiveJwtDecoder.setJwtValidator(defaultValidator.get());
//        return nimbusReactiveJwtDecoder;
//    }

}
