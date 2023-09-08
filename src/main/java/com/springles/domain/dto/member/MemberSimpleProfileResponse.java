package com.springles.domain.dto.member;

import com.springles.domain.constants.GameRole;
import com.springles.domain.constants.Level;
import com.springles.domain.constants.ProfileImg;
import com.springles.domain.entity.MemberGameInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class MemberSimpleProfileResponse {
    private String nickname;    // 게임 닉네임
    private String profileImg;
    private Level level; // 유저 레벨
    private Long memberId;

    public static MemberSimpleProfileResponse of(MemberGameInfo memberGameInfo, Long memberId) {
        return MemberSimpleProfileResponse.builder()
                .memberId(memberId)
                .nickname(memberGameInfo.getNickname())
                .profileImg(memberGameInfo.getProfileImg().getFileUrl())
                .level(memberGameInfo.getLevel())
                .build();
    }

}
