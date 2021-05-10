package com.deadbeef.discordsportsbot.domain.apifootball;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Fixture {

    private Long id;
    private String referee;
    private String timezone;
    private String date;
    private Long timestamp;

}
