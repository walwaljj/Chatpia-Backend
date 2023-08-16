package com.springles.service;

import com.springles.domain.dto.chat.MessageRequestDto;

public interface ChatService {

    void messageResolver(MessageRequestDto messageRequestDto);
}
