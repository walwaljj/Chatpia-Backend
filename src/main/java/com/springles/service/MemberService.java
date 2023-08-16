package com.springles.service;

import com.springles.domain.dto.member.MemberCreateRequest;
import com.springles.domain.dto.member.MemberDeleteRequest;
import com.springles.domain.dto.member.MemberUpdateRequest;

public interface MemberService {

    String signUp(MemberCreateRequest memberDto);

    String updateInfo(MemberUpdateRequest memberDto, Long memberId);

    void signOut(MemberDeleteRequest memberDto, Long memberId);

    boolean memberExists(String memberName);
}
