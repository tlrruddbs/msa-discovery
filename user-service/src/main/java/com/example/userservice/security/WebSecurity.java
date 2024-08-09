package com.example.userservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class WebSecurity {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers("/h2-console/**").permitAll()  // H2 콘솔에 대한 접근 허용
            .requestMatchers("/user-service/**").permitAll()  // /users/** 경로에 대한 접근 허용

            .anyRequest().authenticated()  // 나머지 요청은 인증 필요
        )
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.disable())  // 프레임 옵션 비활성화
        );

    return http.build();
  }
}