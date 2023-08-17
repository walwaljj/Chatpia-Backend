package com.springles.config;

import com.springles.jwt.JwtTokenFilter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authHttp -> authHttp
                                 .requestMatchers(new AntPathRequestMatcher(("/member/info"))).authenticated()
                                .requestMatchers(new AntPathRequestMatcher("/member/login")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/member/signup")).permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtTokenFilter, AuthorizationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
