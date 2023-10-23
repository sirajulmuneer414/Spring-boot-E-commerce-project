package com.sirajul.lenscraft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer ignoreWebSecurity(){
        return (web -> web.ignoring().requestMatchers("/images/**","/css/**"));
    }
}
