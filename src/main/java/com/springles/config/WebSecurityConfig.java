package com.springles.config;

//import com.springles.jwt.AccessTokenInterceptor;
import com.springles.jwt.JwtExceptionFilter;
import com.springles.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                                /** 아래 API를 제외한 나머지에 대해서는 인증이 필요 없도록 임시 세팅 (*추후 개발 완료 시 '실제 권한 코드'로 변경 필요) */
                                .requestMatchers(new AntPathRequestMatcher("/member/updateInfo")).authenticated()
                                .requestMatchers(new AntPathRequestMatcher("/member/signOut")).authenticated()
                                .requestMatchers(new AntPathRequestMatcher("/member/logout")).authenticated()
                                .requestMatchers(new AntPathRequestMatcher("/info/profile")).authenticated()
                                .requestMatchers(new AntPathRequestMatcher("/record", "GET")).authenticated()
                                .anyRequest().permitAll()
                                /** 실제 권한 코드 */
//                                .requestMatchers(new AntPathRequestMatcher("/v1/signup")).permitAll()
//                                .requestMatchers(new AntPathRequestMatcher("/member/signup")).permitAll()
//                                .requestMatchers(new AntPathRequestMatcher("/v1/login-page")).permitAll()
//                                .requestMatchers(new AntPathRequestMatcher("/v1/login")).permitAll()
//                                .requestMatchers(new AntPathRequestMatcher("/member/login")).permitAll()
//                                .requestMatchers(new AntPathRequestMatcher("/v1/login-page?error")).permitAll()
//                                .requestMatchers(new AntPathRequestMatcher("/css/*")).permitAll()
//                                .anyRequest().authenticated()
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
