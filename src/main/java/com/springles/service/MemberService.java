package com.springles.service;

import com.springles.domain.dto.member.MemberCreateResponse;
import com.springles.domain.dto.member.MemberDeleteResponse;
import com.springles.domain.dto.member.MemberUpdateResponse;

public interface MemberService {

    String signUp(MemberCreateResponse memberDto);

    String updateInfo(MemberUpdateResponse memberDto, Long memberId);

    void signOut(MemberDeleteResponse memberDto, Long memberId);

    boolean memberExists(String memberName);
}
