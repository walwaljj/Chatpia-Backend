package com.springles.repository.custom;

import com.springles.domain.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberCustomRepository {
    List<Member> findAllByMemberNameContainingIgnoreCase(String nickname);
}
