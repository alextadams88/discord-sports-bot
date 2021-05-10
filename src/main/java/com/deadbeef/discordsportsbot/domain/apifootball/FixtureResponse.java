package com.deadbeef.discordsportsbot.domain.apifootball;

import lombok.Data;

@Data
public class FixtureResponse {

    private Fixture fixture;
    private Teams teams;
    private Goals goals;

    @Data
    public static class Teams {
        private Team home;
        private Team away;
    }

    @Data
    public static class Goals {
        private Integer home;
        private Integer away;
    }
}
