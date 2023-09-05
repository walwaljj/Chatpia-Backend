package com.springles.domain.dto.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberVertifPwRequest {



    @NotBlank(message = "이메일을 입력해주세요.##")
    @Email(message = "이메일 형식이 올바르지 않습니다.##")
    private String email;

    @NotBlank(message = "아이디를 입력해주세요.##")
    private String memberName;
}
