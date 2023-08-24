package com.springles.domain.constants;

import lombok.Getter;

@Getter
public enum ProfileImg {
    PROFILE01("static/image/profile_01.jpg"),
    PROFILE02("static/image/profile_02.jpg"),
    PROFILE03("static/image/profile_03.jpg"),
    PROFILE04("static/image/profile_04.jpg"),
    PROFILE05("static/image/profile_05.jpg"),
    PROFILE06("static/image/profile_06.jpg");

    private String fileUrl;

    ProfileImg(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
