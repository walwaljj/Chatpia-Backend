package com.springles.repository;

import com.springles.redis.Redisroom;
import org.springframework.data.repository.CrudRepository;

public interface ChatRoomRedisRepository extends CrudRepository<Redisroom, String> {
}
