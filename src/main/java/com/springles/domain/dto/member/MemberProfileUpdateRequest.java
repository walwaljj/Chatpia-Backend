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
public class MemberProfileUpdateRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10)
    private String nickname;

    @NotNull
    private int profileImgNum;

    private ProfileImg profileImg;

    public MemberGameInfo updateMemberGameInfo(MemberGameInfo memberGameInfo, MemberProfileUpdateRequest memberDto) {

        if (memberDto.getProfileImgNum() == 1) {
            profileImg = ProfileImg.PROFILE01;
        } else if (memberDto.getProfileImgNum() == 2) {
            profileImg = ProfileImg.PROFILE02;
        } else if (memberDto.getProfileImgNum() == 3) {
            profileImg = ProfileImg.PROFILE03;
        } else if (memberDto.getProfileImgNum() == 4) {
            profileImg = ProfileImg.PROFILE04;
        } else if (memberDto.getProfileImgNum() == 5) {
            profileImg = ProfileImg.PROFILE05;
        } else if (memberDto.getProfileImgNum() == 6) {
            profileImg = ProfileImg.PROFILE06;
        }

        return MemberGameInfo.builder()
                    .id(memberGameInfo.getId())
                    .memberId(memberGameInfo.getMemberId())
                    .nickname(memberDto.getNickname())
                    .profileImg(profileImg)
                    .level(memberGameInfo.getLevel())
                    .exp(memberGameInfo.getExp())
                    .inGameRole(memberGameInfo.getInGameRole())
                    .build();
    }
}