package com.springles.repository;

import com.springles.domain.entity.MemberGameInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberGameInfoJpaRepository extends JpaRepository<MemberGameInfo, Long> {
    Optional<MemberGameInfo> findByMemberId(Long memberId);

    @Query(value =
            "SELECT ranking " +
                    "FROM (" +
                    "SELECT *, (DENSE_RANK() OVER(ORDER BY exp DESC))" +
                    "AS ranking " +
                    "FROM member_game_info" +
                    ") " +
                    "AS list " +
                    "WHERE member_id = ?"
            , nativeQuery = true)
    Long findByMemberRank(@Param("memberId") Long memberId);

    boolean existsByMemberId(Long memberId);
}
