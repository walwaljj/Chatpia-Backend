package com.springles.service;

import com.springles.domain.dto.member.MemberResponse;

public interface MemberService {

    public String signUp(MemberResponse member);

    public String updateInfo(MemberResponse memberDetails, Long memberId);

    public String signOut(MemberResponse memberDetails, Long memberId);

    public boolean memberExists(String memberName);
}
