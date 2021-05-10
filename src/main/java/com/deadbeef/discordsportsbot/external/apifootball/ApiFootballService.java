package com.deadbeef.discordsportsbot.external.apifootball;

import static com.deadbeef.discordsportsbot.external.apifootball.ApiFootballConstants.*;
import static com.deadbeef.discordsportsbot.external.apifootball.ApiFootballConstants.FixturesConstants.*;
import static com.deadbeef.discordsportsbot.external.apifootball.ApiFootballConstants.FixturesConstants.EventsConstants.*;

import com.deadbeef.discordsportsbot.domain.redis.Event;
import com.deadbeef.discordsportsbot.domain.redis.Fixture;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.PostConstruct;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiFootballService {

    @Value("${api.api-football}")
    private String apiFootballToken;

    private static final int HTTP_TIMEOUT = 30 * 1000;

    @PostConstruct
    private void init(){
    }

    public List<Fixture> getFixtures(LocalDate date, Long leagueId, Integer season){
        var request = buildRequest(FIXTURES_PATH)
            .queryString(DATE, date.toString())
            .queryString(LEAGUE, leagueId)
            .queryString(SEASON, season);

        var response = sendRequest(request);

        try{
            var fixtures = response.getObject().getJSONArray("response");
            return ApiFootballTypeUtil.convertToFixtureList(fixtures);
        }
        catch (JSONException ex){
            log.error("", ex);
            throw new RuntimeException("help");
        }
    }

    public List<Event> getEvents(Long fixtureId){
        var request = buildRequest(EVENTS_PATH)
            .queryString(FIXTURE_ID, fixtureId);

        var response = sendRequest(request);

        try{
            var events = response.getObject().getJSONArray("response");
            return ApiFootballTypeUtil.convertToEventsList(events);
        }
        catch (JSONException ex){
            log.error("", ex);
            throw new RuntimeException("help");
        }
    }

    private HttpRequest buildRequest(String path) {
        return Unirest.get(path)
            .header(API_FOOTBALL_AUTH_HEADER, apiFootballToken)
            .connectTimeout(HTTP_TIMEOUT);
    }

    private JsonNode sendRequest(HttpRequest request){
        HttpResponse<JsonNode> response = request.asJson();
        if (!response.isSuccess()){
            log.error(response.toString());
            throw new RuntimeException("help");
        }

        var responseJson = response.getBody();
        //their error handling is stupid, it looks like if there are *no* errors then "errors" is an empty array, but if there are errors then it's a single object
        try {
            JSONArray errorsArray = responseJson.getObject().getJSONArray("errors");
            if (!errorsArray.isEmpty()){
                log.error(errorsArray.toString());
                throw new RuntimeException("help");
            }
        }
        catch (JSONException ex){
            //do nothing because this means there are no errors
        }
        try {
            JSONObject errors = responseJson.getObject().getJSONObject("errors");
            if (!errors.isEmpty()){
                log.error(errors.toString());
                throw new RuntimeException("help");
            }
        }
        catch (JSONException ex){
            //do nothing because this is expected in 99% of cases
        }

        return responseJson;
    }

}
