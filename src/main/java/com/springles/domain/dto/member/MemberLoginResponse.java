package com.springles.domain.dto.member;

import com.springles.domain.entity.RefreshToken;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberLoginResponse {
    private String memberName;
    private String accessToken;
    private RefreshToken refreshToken;

    @Override
    public String toString() {
        return "memberName : " + memberName
                + ", accessToken : " + accessToken
                + ", refreshToken : "
                + "{ "
                + "id : " + refreshToken.getId()
                + ", refreshToken : " + refreshToken.getRefreshToken()
                + ", expiryDate : " + refreshToken.getExpiration()
                + " }";
    }
}
