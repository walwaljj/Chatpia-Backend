package com.springles.service;

import com.springles.domain.entity.Member;
import org.springframework.stereotype.Service;

@Service
public interface JwtTokenService {

    String reissue(String refreshTokenId);

}
