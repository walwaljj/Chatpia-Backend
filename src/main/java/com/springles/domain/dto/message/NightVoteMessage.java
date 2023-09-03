package com.springles.domain.dto.message;

import com.springles.domain.constants.GameRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NightVoteMessage {
    private Long roomId;
    private Map<GameRole, String> roleVoteResult;
}
