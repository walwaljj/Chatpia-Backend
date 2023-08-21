package com.springles.controller.ui;

import com.google.gson.Gson;
import com.springles.websocket.SimpleChatHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatUiController {
    private final SimpleChatHandler simpleChatHandler;
    private final Gson gson;

    @GetMapping("rooms")
    public String rooms() {
        return "rooms";
    }

    @GetMapping("enter")
    public String enter(@RequestParam("username") String username) {
        return "chat";
    }

    @GetMapping
    public String index() {
        return "chat-lobby";
    }

    @GetMapping("{roomId}/{userId}")
    public String enterRoom(){
        return "chat-room";
    }
}