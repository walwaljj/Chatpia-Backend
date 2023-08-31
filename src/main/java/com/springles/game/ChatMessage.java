package com.springles.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class ChatMessage {

    private String message;
    private String sender;
    private String time;
    private Long roomId;

    public void setTime(String time) {
        this.time = time;
    }
}
