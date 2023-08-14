package com.springles.domain.dto.Chat;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {

    public enum State { OPEN, DELETE, PLAY }
    public enum Secret { PUBLIC, SECRET }

    private String id;
    private String title;
    private String password;
    private String owner;
    private State state;
    private Long capacity;
    private Long head;
    private Secret secret;

    public static ChatRoom create(String name) {
        return ChatRoom.builder()
            .id(UUID.randomUUID().toString())
            .title(name)
            .password("testpassword")
            .head(1L)
            .owner("testUser")
            .capacity(1L)
            .secret(Secret.PUBLIC)
            .state(State.OPEN)
            .build();
    }
}
