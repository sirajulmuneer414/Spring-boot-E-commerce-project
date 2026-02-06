package com.sirajul.lenscraft.config;

import com.sirajul.lenscraft.entity.user.UserInformation;
import com.sirajul.lenscraft.entity.user.enums.Role;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SecurityConfig {

        @Autowired
        UserDetailsService userDetailsService;
        @Autowired
        SuccessHandler successHandler;
        @Autowired
        CustomAuthenticationFailureHandler failureHandler;

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService)
                        throws Exception {
                log.info("inside security filter chain");
                http.authorizeHttpRequests(
                                auth -> auth.requestMatchers("/admin/**").hasAuthority(Role.ADMIN.name())
                                                .requestMatchers("/user/**", "/order/**")
                                                .hasAnyAuthority(Role.ADMIN.name(), Role.USER.name(),
                                                                Role.SELLER.name())
                                                .requestMatchers("/**", "/register", "/verification",
                                                                "/forgot-password/**", "/temp")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(
                                                form -> form.loginPage("/login")
                                                                .successHandler(successHandler)
                                                                .failureHandler(failureHandler)
                                                                .permitAll())
                                .logout(
                                                logout -> logout
                                                                .logoutUrl("/logout")
                                                                .logoutSuccessUrl("/")
                                                                .invalidateHttpSession(true)
                                                                .deleteCookies("JSESSIONID"))
                                .rememberMe(
                                                me -> me
                                                                .rememberMeServices(
                                                                                rememberMeServices(userDetailsService))
                                                                .key("Token").userDetailsService(userDetailsService));
                return http.build();
        }

        @Bean
        public WebSecurityCustomizer ignoreWebSecurity() {
                return (web -> web.ignoring().requestMatchers("/images/**", "/css/**", "/profilePic/**"));
        }

        @Bean
        public RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
                TokenBasedRememberMeServices tokenBasedRememberMeServices = new TokenBasedRememberMeServices("Token",
                                userDetailsService);

                tokenBasedRememberMeServices.setAlwaysRemember(true);

                return tokenBasedRememberMeServices;
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                log.info("inside dao authentication provider");

                DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

                authenticationProvider.setUserDetailsService(userDetailsService);
                authenticationProvider.setPasswordEncoder(passwordEncoder());

                return authenticationProvider;

        }
}
