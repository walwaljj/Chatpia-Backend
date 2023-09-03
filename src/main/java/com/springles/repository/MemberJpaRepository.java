package com.springles.repository;

import com.springles.domain.entity.Member;
import com.springles.repository.custom.MemberJpaRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long>,
    MemberJpaRepositoryCustom {

    boolean existsByMemberName(String memberName);

    Optional<Member> findByMemberName(String memberName);

    void deleteByMemberName(String memberName);
    List<Member> findAllByEmail(String email);

    Optional<Member> findByMemberNameAndEmail(String memberName, String email);
}
