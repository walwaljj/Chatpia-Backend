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
public class MemberProfileResponse {

    private String nickname;    // 게임 닉네임
    private ProfileImg profileImg;
    private Level level; // 유저 레벨
    private Long exp;   // 유저 경험치
    private GameRole inGameRole;  // 게임 내 직업
    private boolean isObserver; // 게임 내 관찰자 여부
    private Long memberId;

    public static MemberProfileResponse of(MemberGameInfo memberGameInfo, Long memberId) {
        return MemberProfileResponse.builder()
                .memberId(memberId)
                .nickname(memberGameInfo.getNickname())
                .profileImg(memberGameInfo.getProfileImg())
                .level(memberGameInfo.getLevel())
                .exp(memberGameInfo.getExp())
                .inGameRole(memberGameInfo.getInGameRole())
                .isObserver(memberGameInfo.isObserver())
                .build();
    }
}
