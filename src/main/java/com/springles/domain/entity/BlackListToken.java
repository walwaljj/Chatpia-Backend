package com.springles.domain.entity;

import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Builder
@RedisHash(value = "blacklist")
public class BlackListToken {

    @Id
    private String id;

    @Indexed
    private String accessToken;

    @TimeToLive
    private Long expiration;
}
