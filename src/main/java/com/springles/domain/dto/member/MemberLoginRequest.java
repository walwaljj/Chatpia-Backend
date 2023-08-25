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
public class MemberLoginRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String memberName;
    private String password;
}
