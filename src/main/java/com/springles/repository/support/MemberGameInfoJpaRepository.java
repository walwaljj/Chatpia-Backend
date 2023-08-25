package com.springles.repository.support;

import com.springles.domain.entity.MemberGameInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberGameInfoJpaRepository extends JpaRepository<MemberGameInfo, Long> {
    Optional<MemberGameInfo> findByMemberId(Long memberId);
}
