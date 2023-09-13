package com.springles.repository;

import com.springles.domain.entity.Member;
import com.springles.domain.entity.MemberGameInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberGameInfoJpaRepository extends JpaRepository<MemberGameInfo, Long> {

    /**
     * memberId에 해당하는 MemberGameInfo 반환
     * */
    Optional<MemberGameInfo> findByMemberId(Long memberId);

    /**
     * memberId가 존재할 경우, 해당 memberId를 반환
     * ajax가 Long -> boolean으로 형변환하지 못하는 관계로 true/false가 아닌 memberId/null로 MemberGameInfo 존재여부 판별
     */
    @Query(value = "SELECT member_game_info_id FROM member_game_info WHERE member_id = ?", nativeQuery = true)
    Long existsByMemberId(@Param("memberId") Long memberId);

    /**
     * memberId에 해당하는 ranking 반환
     * */
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

    Optional<MemberGameInfo> findByNickname(String nickname);
}
