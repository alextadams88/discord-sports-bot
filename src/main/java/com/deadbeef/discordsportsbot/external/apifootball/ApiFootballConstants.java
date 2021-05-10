package com.deadbeef.discordsportsbot.external.apifootball;

public class ApiFootballConstants {

    public static final String API_FOOTBALL_BASE_URL = "https://v3.football.api-sports.io";
    public static final String API_FOOTBALL_AUTH_HEADER = "x-apisports-key";


    public static class FixturesConstants {

        public static final String FIXTURES_PATH = API_FOOTBALL_BASE_URL + "/fixtures";
        public static final String ID = "id";
        public static final String LEAGUE = "league";
        public static final String TEAM = "team";
        public static final String DATE = "date";
        public static final String SEASON = "season";


        public static class EventsConstants {
            public static final String EVENTS_PATH = FIXTURES_PATH + "/events";
            public static final String TEAM = "team";
            public static final String FIXTURE_ID = "fixture";
        }

    }
}
