package com.springles.config;

//import com.springles.jwt.AccessTokenInterceptor;
//import com.springles.jwt.CORSFilter;
import com.springles.jwt.JwtExceptionFilter;
import com.springles.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig implements WebMvcConfigurer {

    private final JwtTokenFilter jwtTokenFilter;
    private final JwtExceptionFilter jwtExceptionFilter;

//    private final AccessTokenInterceptor accessTokenInterceptor;
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(this.accessTokenInterceptor);
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authHttp -> authHttp
                                /** 실제 권한 코드 */
                                // 회원가입, 로그인, 아이디/비밀번호 찾기, 쿠키 저장 api·ui
                                .requestMatchers(new AntPathRequestMatcher("/v1/signup")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v1/login")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v1/login-page")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v1/login-page?error")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v1/vertification-id")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v1/vertification-pw")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/member/signup")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/member/login")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/member/vertification/id")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/member/vertification/pw")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/cookie/*")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/css/*")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/images/*")).permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(
                        login -> login
                                .loginPage("/v1/login-page")
                                .successHandler(new LoginSuccessHandler())
                                .failureHandler(new LoginFailureHandler())
                                .permitAll()
                )
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtTokenFilter, AuthorizationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtTokenFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
