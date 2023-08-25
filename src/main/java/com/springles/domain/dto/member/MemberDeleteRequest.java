package com.springles.domain.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDeleteRequest {

    // 게임 아이디
    private String memberName;

    // 비밃번호
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}