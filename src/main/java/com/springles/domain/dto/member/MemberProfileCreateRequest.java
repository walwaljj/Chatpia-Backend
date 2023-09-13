package com.springles.domain.dto.member;

import com.springles.domain.constants.GameRole;
import com.springles.domain.constants.Level;
import com.springles.domain.constants.ProfileImg;
import com.springles.domain.entity.Member;
import com.springles.domain.entity.MemberGameInfo;
import com.springles.valid.ValidationGroups;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileCreateRequest {

    @NotBlank(message = "닉네임을 입력해주세요.##", groups = ValidationGroups.NotEmptyGroup.class)
    @Size(min = 2, max = 10, message = "닉네임은 2 ~ 10자 사이여야 합니다.##", groups = ValidationGroups.SizeCheckGroup.class)
    @Column(unique = true)
    private String nickname;

    @NotNull(message = "프로필 이미지를 선택해주세요.##", groups = ValidationGroups.NotEmptyGroup.class)
    private int profileImgNum;

    private ProfileImg profileImg;

    public MemberGameInfo newMemberGameInfo(MemberProfileCreateRequest memberDto, Member member) {

        if(memberDto.getProfileImgNum() == 1) {
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
                .nickname(memberDto.getNickname())
                .profileImg(profileImg)
                .level(Level.BEGINNER)
                .exp(0L)
                .inGameRole(GameRole.NONE)
                .member(member)
                .build();
    }
}
