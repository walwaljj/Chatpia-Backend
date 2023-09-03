package com.springles.domain.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DayDiscussionMessage {
    private Long roomId;
    private List<Long> suspiciousList;
}
