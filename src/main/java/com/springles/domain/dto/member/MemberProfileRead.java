package com.springles.domain.dto.member;

import com.springles.domain.constants.ProfileImg;
import com.springles.domain.entity.MemberGameInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileRead {

    private String nickname;
    private ProfileImg profileImg;
    private Long level;
    private Long exp;
    private Long nextLevel;
}