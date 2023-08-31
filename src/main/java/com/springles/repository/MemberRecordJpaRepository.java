package com.springles.repository;

import com.springles.domain.entity.MemberRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRecordJpaRepository extends JpaRepository<MemberRecord, Long> {
    Optional<MemberRecord> findByMemberId(Long memberId);
}
