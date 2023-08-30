package com.springles.game;

import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.exception.CustomException;
import com.springles.exception.constants.ErrorCode;
import com.springles.jwt.JwtTokenUtils;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@RequiredArgsConstructor
public class MessageInterceptor implements ChannelInterceptor {

    private final JwtTokenUtils tokenUtils;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
            StompHeaderAccessor.class);

        if (accessor.getCommand().equals(StompCommand.CONNECT)) {
            String accessToken = accessor.getFirstNativeHeader("Authorization");
            if (accessToken == null || !tokenUtils.validate(accessToken)) {
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
