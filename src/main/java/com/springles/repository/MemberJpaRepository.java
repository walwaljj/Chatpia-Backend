package com.springles.repository;

import com.springles.domain.entity.Member;
import com.springles.repository.custom.MemberCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long>, MemberCustomRepository {

    boolean existsByMemberName(String memberName);

    Optional<Member> findByMemberName(String memberName);

    void deleteByMemberName(String memberName);
}
