package com.example.userservice.security;

import com.example.userservice.service.UserService;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurity {

  private final UserService userService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final Environment env;

  public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.env = env;
    this.userService = userService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @Bean
  protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
    // Configure AuthenticationManagerBuilder
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

    // CSRF 비활성화
    http.csrf(csrf -> csrf.disable());

    // 인증 및 권한 설정
    http.authorizeHttpRequests((authz) -> authz
            .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/users/**", "POST")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/users/**", "GET")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/login", "POST")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/welcome", "GET")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/health_check")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
            .requestMatchers("/**").access(
                new WebExpressionAuthorizationManager("hasIpAddress('127.0.0.1') or hasIpAddress('192.168.55.86')"))
            .anyRequest().authenticated()
        )
        .authenticationManager(authenticationManager)
        .exceptionHandling(exceptionHandling -> exceptionHandling
            .authenticationEntryPoint(authenticationEntryPoint()) // 401 Unauthorized 설정
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // Custom Authentication Filter 추가
    http.addFilterBefore(getAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);

    http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));


    return http.build();
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED); // 401 반환
  }

  private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
    AuthenticationFilter filter = new AuthenticationFilter(authenticationManager, userService, env);
    filter.setAuthenticationManager(authenticationManager);
    return filter;
  }
}