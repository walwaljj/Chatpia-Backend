package com.springles.domain.constants;

import lombok.Getter;

@Getter
public enum ProfileImg {
//    PROFILE01("../images/profile_01.jpg"),
//    PROFILE02("../images/profile_02.jpg"),
//    PROFILE03("../images/profile_03.jpg"),
//    PROFILE04("../images/profile_04.jpg"),
//    PROFILE05("../images/profile_05.jpg"),
//    PROFILE06("../images/profile_06.jpg");

    PROFILE01("/images/profile_01.jpg"),
    PROFILE02("/images/profile_02.jpg"),
    PROFILE03("/images/profile_03.jpg"),
    PROFILE04("/images/profile_04.jpg"),
    PROFILE05("/images/profile_05.jpg"),
    PROFILE06("/images/profile_06.jpg");


    private String fileUrl;

    ProfileImg(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
