package com.springles.domain.entity;

import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Builder
@RedisHash(value = "token")
public class RefreshToken {

    @Id
    private String Id;
    @Indexed
    private String memberName;
    private String refreshToken;    // UUID
    @TimeToLive
    private Long expiration;
}
