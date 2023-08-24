package com.springles.domain.entity;

import com.springles.domain.constants.GameRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@RedisHash(value = "Player")
public class Player {

    @Id
    private Long memberId;

    @Indexed
    private Long roomId;

    @Enumerated(EnumType.STRING)
    private GameRole role;

    public static Player of(Long memberId, Long roomId) {
        return Player.builder()
            .memberId(memberId)
            .roomId(roomId)
            .role(GameRole.NONE)
            .build();
    }

    public void updateRole(GameRole role) {
        this.role = role;
    }

}
