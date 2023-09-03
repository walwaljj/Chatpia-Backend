package com.springles.repository.custom;

import com.springles.domain.entity.Member;

import java.util.List;

public interface MemberJpaRepositoryCustom {
    List<Member> findAllByMemberNameContainingIgnoreCase(String nickname);
}
