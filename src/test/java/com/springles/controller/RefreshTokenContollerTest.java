/*
package com.springles.controller;

import com.springles.controller.api.RefreshTokenContoller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RefreshTokenContollerTest {


    @InjectMocks
    private RefreshTokenContoller refreshTokenContoller;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(refreshTokenContoller).build();
    }

    @Test
    @DisplayName("accessToken 재발급 테스트 - CASE.성공")
    void reissue() throws Exception {
        // given
        String refreshTokenId = "db2802bd-ec7a-46fb-8c28-708d4a23e1a7";
        String newAccessToken = "woqkfrmqehlsdjtptmxhzmsd";
        String returnValue = "accessToken : " + newAccessToken;

        // when
        when(refreshTokenService.reissue(refreshTokenId)).thenReturn(newAccessToken);

        // then
        mockMvc.perform(post("/token/reissue")
                        .param("refreshTokenId", refreshTokenId)
                )
                .andExpectAll(
                        status().is2xxSuccessful(),
                        MockMvcResultMatchers.jsonPath("$.data").value(returnValue)
                );
    }
}*/
