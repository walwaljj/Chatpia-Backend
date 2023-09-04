package com.springles.domain.dto.response;

import com.springles.domain.constants.GameRole;
import com.springles.domain.entity.Player;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SuspectVoteRes {
    private Long vote;
    private boolean isMafia; // 죽은 애가 마피아인지

    public static SuspectVoteRes of(Player suspect) {
        SuspectVoteRes suspectVoteRes = new SuspectVoteRes();
        suspectVoteRes.vote = suspect.getMemberId();
        suspectVoteRes.isMafia = suspect.getRole() == GameRole.MAFIA ? true : false;
        return suspectVoteRes;
    }
}
