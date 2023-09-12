package com.springles.domain.dto.member;

import com.springles.domain.constants.ProfileImg;
import com.springles.domain.entity.MemberGameInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;
@Getter
@Builder
public class PlayerInfoResponse {
    // 사용자 ID
    private Long id;
    // 사용자 게임 네임
    private String nickName;
    // 프로필 이미지
    private ProfileImg profileImg;
    // 방장 여부
    private Boolean isOwner;
    // 레벨 이미지
    private String levelImg;


    public static PlayerInfoResponse of(Long memberId, Long ownerId, MemberGameInfo gameInfo) {
        return PlayerInfoResponse.builder()
                .id(memberId)
                .nickName(gameInfo.getNickname())
                .profileImg(gameInfo.getProfileImg())
                .levelImg(gameInfo.getLevel().getImgUrl())
                .isOwner(Objects.equals(memberId, ownerId))
                .build();
    }
}
