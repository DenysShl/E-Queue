package com.example.den.equeue.config;

import com.example.den.equeue.security.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    private static final String[] AUTH_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/login/**",
            "/register/**",
            "/static/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/h2-console/**",
            "/error/**",
            "/actuator/**",
            "/api/auth/**",
            "/api/user/**"
    };

    @Bean
    @Order(1)
    SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.GET, AUTH_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.POST, "/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(customizer -> customizer.logoutSuccessUrl("/").permitAll())
                .exceptionHandling(customizer -> customizer
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(customerizer -> customerizer.successHandler((successHandler)));

        return http.build();
    }
}
