package com.springles.redis;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@RedisHash("Redisroom")
@Builder
@Data
public class Redisroom {
    @Id
    private String id;

    private String topic;

    @Override
    public String toString() {
        return "RedisChatRoom{" +
            "id=" + id +
            ", topic=" + topic +
            '}';
    }
}
