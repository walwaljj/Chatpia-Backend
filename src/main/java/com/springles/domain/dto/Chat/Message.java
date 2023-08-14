package com.springles.domain.dto.Chat;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    public enum MessageType { ENTER, EXIT, MESSAGE }

    public MessageType type;
    private String roomId;
    private String senderName;
    private String message;
    private Date time;

    public static void of() {

    }
}