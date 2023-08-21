package com.springles.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("v1/home")
public class ChatRoomUiController {
    @GetMapping("")
    public String home(Model model) {
        model.addAttribute("data", "Hello");
        return "home";
    }
}
