package com.springles.service;

import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenService {

    String reissue(String refreshTokenId);

}
