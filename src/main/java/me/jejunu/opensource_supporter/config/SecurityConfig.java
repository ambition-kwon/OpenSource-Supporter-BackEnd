package me.jejunu.opensource_supporter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    protected SecurityFilterChain config(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.oauth2Login()
                .authorizationEndpoint()
                .baseUri("/login");
        return httpSecurity.build();
    }
}
