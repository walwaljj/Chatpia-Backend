package com.springles.game;

import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.jwt.JwtTokenUtils;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
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
public class MessageInterceptor implements ChannelInterceptor {

    private final JwtTokenUtils tokenUtils;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
            StompHeaderAccessor.class);

        if (accessor.getCommand().equals(StompCommand.CONNECT)) {
            String accessToken = accessor.getFirstNativeHeader("Authorization");
            /** tokenUtils.validate 메소드가 'boolean 값 반환 -> exception 케이스에 따른 int 타입 반환'으로 변경되어 코드 수정
             * 0 : 유효하지 않은 JWT 서명, 지원되지 않는 JWT토큰, 잘못된 JWT 토큰
             * 1 : 유효한 토큰
             * 2 : 유효기간이 만료된 토큰
             *  */
            if (accessToken == null || tokenUtils.validate(accessToken) != 1) {
                throw new CustomException(ErrorCode.NOT_AUTHORIZED_TOKEN);
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                MemberCreateRequest.builder()
                    .memberName(tokenUtils.parseClaims(accessToken).getSubject())
                    .build(),
                accessToken, new ArrayList<>()
            );

            accessor.setUser(authenticationToken);
        }
        return message;
    }
}
