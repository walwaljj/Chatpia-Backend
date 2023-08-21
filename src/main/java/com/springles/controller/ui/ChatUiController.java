package com.springles.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("v1/chat")
@Controller
public class ChatUiController {
    // 채팅방 들어갈 때
    @GetMapping("")
    public String enterRoom(
            ){
        return "chat-room";
    }

}
