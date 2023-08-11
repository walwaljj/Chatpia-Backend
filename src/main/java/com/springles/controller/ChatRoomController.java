package com.springles.controller;

import com.springles.domain.dto.Chat.ChatRoom;
import com.springles.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class ChatRoomController {

    private final ChatService chatService;

    @GetMapping("/all")
    public List<ChatRoom> room() {
        return chatService.findAllRoom();
    }

    @PostMapping
    public ChatRoom createRoom(@RequestParam String name) {
        return chatService.createChatRoom(name);
    }

    @GetMapping("/roomId/{roomId}")
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatService.findRoomById(roomId);
    }
}
