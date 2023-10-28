package com.sirajul.lenscraft.config;

import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired UserDetailsService userDetailsService;
    @Autowired
    SuccessHandler successHandler;
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public WebSecurityCustomizer ignoreWebSecurity(){
        return (web -> web.ignoring().requestMatchers("/images/**","/css/**","/profilePic/**"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http.authorizeHttpRequests(
                auth ->
                        auth.requestMatchers("/admin/**").hasAuthority(Role.ADMIN.name())
                                .requestMatchers("/user/**").hasAnyAuthority(Role.ADMIN.name(),Role.USER.name(),Role.SELLER.name())
                                .requestMatchers("/**","/register","/verification").permitAll()
                                .anyRequest().authenticated()
        )
                .formLogin(
                        form ->
                                form.loginPage("/login").permitAll()
                                        .successHandler(successHandler)
                                        .permitAll()
                )
                .logout(
                        logout ->
                                logout
                                        .logoutUrl("/logout")
                                        .logoutSuccessUrl("/shop")
                                        .invalidateHttpSession(true)
                                        .deleteCookies("JSESSIONID")
                )
                .rememberMe(
                        me ->
                                me
                                        .rememberMeServices(rememberMeServices(userDetailsService))
                                        .key("Token").userDetailsService(userDetailsService)
                );
        return http.build();
    }

    @Bean
    public RememberMeServices rememberMeServices(UserDetailsService userDetailsService){
        TokenBasedRememberMeServices tokenBasedRememberMeServices =
                new TokenBasedRememberMeServices("Token",userDetailsService);

        tokenBasedRememberMeServices.setAlwaysRemember(true);

        return tokenBasedRememberMeServices;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserInformation user){

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;

    }
}
