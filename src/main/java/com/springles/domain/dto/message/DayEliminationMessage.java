package com.springles.domain.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DayEliminationMessage {
    private Long roomId;
    private Long deadPlayerId;
}
