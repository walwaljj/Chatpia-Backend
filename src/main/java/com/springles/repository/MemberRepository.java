package com.springles.repository;


import com.springles.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<List<Member>> findAllByMemberNameContainingIgnoreCase(String nickname);
}
