package com.springles.domain.dto.member;

import com.springles.domain.constants.ProfileImg;
import com.springles.domain.entity.MemberGameInfo;
import com.springles.valid.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileUpdateRequest {

    @NotBlank(message = "닉네임을 입력해주세요.##", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min = 2, max = 10, message = "닉네임은 2 ~ 10자 사이여야 합니다.##", groups = ValidationGroups.SizeCheckGroup.class)
    private String nickname;

    @NotNull(message = "프로필 이미지를 선택해주세요.##", groups = ValidationGroups.NotEmptyGroup.class)
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
                    .isObserver(memberGameInfo.isObserver())
                    .build();
    }
}