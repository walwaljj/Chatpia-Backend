package com.springles.domain.dto.cookie;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CookieSetRequest {

    private String key;
    private String value;
}
