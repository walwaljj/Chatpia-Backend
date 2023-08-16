package com.springles.domain.dto.member;

import com.springles.domain.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberDeleteResponse {

    // 게임 아이디
    private String memberName;

    // 비밃번호
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}