package com.springles.domain.dto.member;

import com.springles.valid.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDeleteRequest {

    // 게임 아이디
    private String memberName;

    // 비밃번호
    @NotBlank(message = "비밀번호를 입력해주세요.##", groups = ValidationGroups.NotEmptyGroup.class)
    private String password;
}