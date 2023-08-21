package com.springles.repository;

import com.springles.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    boolean existsByMemberName(String memberName);

    Optional<Member> findByMemberName(String memberName);

    void deleteByMemberName(String memberName);
}
