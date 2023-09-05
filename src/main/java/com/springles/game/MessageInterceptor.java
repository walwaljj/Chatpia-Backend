package com.springles.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.jwt.JwtTokenUtils;
import java.util.ArrayList;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class MessageInterceptor implements ChannelInterceptor {

    private final JwtTokenUtils tokenUtils;
    private final ObjectMapper objectMapper;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        return message;
    }
}
