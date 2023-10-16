package com.tiddev.authorization.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@EnableWebSecurity
public class SecurityConfig {

    /**
     * First will be applied the OAuth2 security filters configuration.
     * In this configuration, I only indicate that all the failing request will be redirected to the /login page.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.build();
    }
//    curl -iX POST http://192.168.102.82:8092/oauth2/token -d "client_id=gateway-client-id&client_secret=123456789&grant_type=client_credentials"
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
//        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                // client-id and client-secret that must be used from all the OAuth2 clients
//                .clientId("gateway-client-id")
//                .clientSecret("123456789")
                // the Basic authentication method will be used between backend-gateway-client and backend-auth
//                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                // grant types to be used
//                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
//                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                // permitted redirect URI after the authentication is successful
//                .redirectUri("http://backend-gateway-client:8083/login/oauth2/code/gateway")
//                .redirectUri("http://backend-gateway-client:8083/authorized")
                // acceptable scopes for the authorization
//                .scope(OidcScopes.OPENID)
//                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.of(50000L, ChronoUnit.SECONDS)).build())
//                .scope("message.read")
//                .scope("message.write")
//                .build();
        //        jdbcRegisteredClientRepository.save(registeredClient);
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    /**
     * Acceptable URL of the authorization server
     */
    @Bean
    public ProviderSettings providerSettings(@Value("${provider.url}") String pUrl) {
        return ProviderSettings.builder()
                .issuer(pUrl)
                .build();
    }
}
