package com.springles.domain.entity;

import com.springles.domain.constants.GameRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@RedisHash(value = "Player")
public class Player {

    @Id
    @Indexed
    private Long memberId;

    @Indexed
    private Long roomId;

    @Enumerated(EnumType.STRING)
    private GameRole role;

    private boolean alive;

    @Indexed
    private String memberName;

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setRole(GameRole role) {
        this.role = role;
    }

    public static Player of(Long memberId, Long roomId, String memberName) {
        return Player.builder()
            .memberId(memberId)
            .roomId(roomId)
            .role(GameRole.NONE)
            .alive(true)
            .memberName(memberName)
            .build();
    }

    public void updateRole(GameRole role) {
        this.role = role;
    }

}
