package com.springles.repository;

import com.springles.domain.entity.GameRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRecordJpaRepository extends JpaRepository<GameRecord, Long> {
    List<GameRecord> findTOP1ByMemberIdOrderByIdDesc(Long memberId);
}
