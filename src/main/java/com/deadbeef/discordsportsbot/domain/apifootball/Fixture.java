package com.deadbeef.discordsportsbot.domain.apifootball;

import com.google.gson.annotations.SerializedName;
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
    private Status status;

    @Data
    public static class Status {
        @SerializedName("long")
        private String longStatus;
        @SerializedName("short")
        private String shortStatus;
        private String elapsed;
    }

}
