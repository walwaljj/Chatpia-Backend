package com.springles.repository;

import com.springles.domain.entity.Player;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRedisRepository extends CrudRepository<Player,Long> {

    List<Player> findByRoomId(Long roomId);

    int countByRoomId(Long roomId);


    boolean existsByMemberId(Long memberId);

    boolean existsByMemberName(String memberName);

    Optional<Player> findByMemberName(String memberName);
}
