package com.deadbeef.discordsportsbot.external.apifootball;

import com.deadbeef.discordsportsbot.domain.redis.Event;
import com.deadbeef.discordsportsbot.domain.redis.Fixture;
import java.util.List;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

public class ApiFootballTypeUtil {

    public static List<Fixture> convertToFixtureList(JSONArray jsonNode){
        return List.of();
    }

    public static Fixture convertToFixture(JSONObject jsonObject){
        return new Fixture();
    }

    public static List<Event> convertToEventsList(JSONArray jsonNode){
        return List.of();
    }

    public static Event convertToEvent(JSONObject jsonObject){
        return new Event();
    }

}
