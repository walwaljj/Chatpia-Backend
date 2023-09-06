package com.springles.domain.dto.message;

import com.springles.domain.constants.GameRole;
import com.springles.domain.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleExplainMessage {

    GameRole gameRole;
    String message;
    String sender;
    String time;

    public RoleExplainMessage(GameRole gameRole, String time) {
        this.gameRole = gameRole;
        this.message = "당신의 직업은 " + gameRole + "입니다.";
        this.sender = "admin";
        this.time = time;
    }

}
