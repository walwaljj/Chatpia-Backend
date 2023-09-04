package com.springles.domain.dto.member;

import com.springles.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class MemberInfoResponse {

    private Long id;               // 사용자 ID (Sequence 값)
    private String memberName;     // 사용자 닉네임 (가입 시 입력했던 ID)
    private String email;     // 사용자 이메일


    public static MemberInfoResponse of(Member member) {
        return MemberInfoResponse.builder()
                .id(member.getId())
                .memberName(member.getMemberName())
                .email(member.getEmail())
                .build();
    }
}
