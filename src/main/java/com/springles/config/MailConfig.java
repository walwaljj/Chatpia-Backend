package com.springles.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    // username, password 환경변수 설정 필요
    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);

        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        // 사용자 계정 인증 여부
        properties.setProperty("mail.smtp.auth", "true");
        // Socket Read Timeout 시간(ms)
        properties.setProperty("mail.smtp.timeout", "3000");
        // StartTLS 활성화(메일 전송 시 데이터 암호화) 여부
        properties.setProperty("mail.smtp.starttls.enable", "true");
        // debugging 로그 출력 여부
        properties.setProperty("mail.debug", "true");

        return properties;
    }
}
