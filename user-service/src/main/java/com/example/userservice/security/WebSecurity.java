package com.example.userservice.security;

import com.example.userservice.service.UserService;
import java.util.function.Supplier;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@EnableWebSecurity
@Configuration

public class WebSecurity {

  private final UserService userService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final Environment env;

  public static final String ALLOWED_IP_ADDRESS = "127.0.0.1";
  public static final String SUBNET = "/32";
  public static final IpAddressMatcher ALLOWED_IP_ADDRESS_MATCHER = new IpAddressMatcher(ALLOWED_IP_ADDRESS + SUBNET);

  public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.env = env;
    this.userService = userService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @Bean
  protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

    AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

    http.csrf(csrf -> csrf.disable());
    http.csrf(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests((authz) -> authz
        .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/users")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/users/**", HttpMethod.POST.name())).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/welcome")).permitAll()
        .requestMatchers(new AntPathRequestMatcher("/health-check")).permitAll()
        .requestMatchers("/**").access(this::hasIpAddress)
        .anyRequest().authenticated()
    );

    http.authenticationManager(authenticationManager)
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilter(getAuthenticationFilter(authenticationManager));

    http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

    return http.build();
  }

  private AuthorizationDecision hasIpAddress(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
    return new AuthorizationDecision(ALLOWED_IP_ADDRESS_MATCHER.matches(object.getRequest()));
  }

  private AuthenticationFilterNew getAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
    final CustomAuthenticationFilter filter = new CustomAuthenticationFilter(authenticationManager);
    filter.setFilterProcessesUrl("/login");
    return filter;
  }
}