package com.deadbeef.discordsportsbot.domain.apifootball;

import lombok.Data;

@Data
public class Event {

    private Time time;
    private String type;
    private String detail;
    private String comments;
    private Team team;
    private Player player;
    private Player assist;

    @Data
    public static class Time {
        private Integer elapsed;
        private Integer extra;
    }

}
