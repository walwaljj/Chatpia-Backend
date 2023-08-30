package com.springles.config;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeConfig {
    public static LocalDateTime getFinTime(int time) {
        // 한국 시간으로 맞추기
        // 미리 끝나는 시간을 설정하기 때문에 프론트로 timer를 전달할 때까지 약 0,4초 차이라고 가정
        // 정한 시간보다 0.4초 후에 끝내기 위해 400000000ns을 더함
        return LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                .plusSeconds(time).plusNanos(400000000);
    }

    public static Date convertToDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.of("Asia/Seoul")).toInstant());
    }
}
