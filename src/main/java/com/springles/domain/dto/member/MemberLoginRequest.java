package com.springles.domain.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String memberName;
    private String password;
}
