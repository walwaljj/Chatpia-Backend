package com.springles.domain.dto.member;

import com.springles.valid.ValidationGroups;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberVertifIdRequest {
    @NotBlank(message = "이메일을 입력해주세요.##", groups = ValidationGroups.NotEmptyGroup.class)
    @Email(message = "이메일 형식이 올바르지 않습니다.##", groups = ValidationGroups.PatternCheckGroup.class)
    private String email;
}
