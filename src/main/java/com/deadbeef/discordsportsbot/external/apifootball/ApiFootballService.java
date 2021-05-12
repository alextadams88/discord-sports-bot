package com.deadbeef.discordsportsbot.external.apifootball;

import static com.deadbeef.discordsportsbot.external.apifootball.ApiFootballConstants.*;
import static com.deadbeef.discordsportsbot.external.apifootball.ApiFootballConstants.FixturesConstants.*;
import static com.deadbeef.discordsportsbot.external.apifootball.ApiFootballConstants.FixturesConstants.EventsConstants.*;
import static com.deadbeef.discordsportsbot.external.apifootball.ApiFootballConstants.PlayersConstants.PLAYERS_PATH;
import static com.deadbeef.discordsportsbot.external.apifootball.ApiFootballConstants.PlayersConstants.PLAYER_ID;

import com.deadbeef.discordsportsbot.domain.apifootball.FixtureResponse;
import com.deadbeef.discordsportsbot.domain.apifootball.Event;
import com.deadbeef.discordsportsbot.domain.apifootball.Player;
import com.google.gson.Gson;
import java.time.LocalDate;
import java.util.ArrayList;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiFootballService {

    @Value("${api.api-football}")
    private String apiFootballToken;

    private Gson gson = new Gson();

    private static final int HTTP_TIMEOUT = 30 * 1000;

    @PostConstruct
    private void init(){
    }

    public List<FixtureResponse> getFixtures(LocalDate date, Long leagueId, Integer season){
        var request = buildRequest(FIXTURES_PATH)
            .queryString(DATE, date.toString())
            .queryString(LEAGUE, leagueId)
            .queryString(SEASON, season);

        log.info("Sending getFixtures request to API Football service. Date=[{}] League=[{}] Season=[{}]", date, leagueId, season);

        var response = sendRequest(request);

        try{
            var fixturesJson = response.getObject().getJSONArray("response");

            List<FixtureResponse> fixtures = new ArrayList<>();
            for (int i = 0; i < fixturesJson.length(); i++){
                var result = convert(fixturesJson.getJSONObject(i), FixtureResponse.class);
                fixtures.add(result);
            }

            return fixtures;
        }
        catch (JSONException ex){
            log.error("Error parsing Fixtures response from API Football service. Response=[{}] ", response.toString());
            throw new RuntimeException(ex);
        }
    }

    public FixtureResponse getFixtureById(Long fixtureId){
        var request = buildRequest(FIXTURES_PATH)
            .queryString(ID, fixtureId);

        log.info("Sending getFixtureByID request to API Football service. FixtureID=[{}]", fixtureId);

        var result = sendFixturesRequest(request);
        if (result.size() < 1){
            log.error("No fixture received from getFixtureById request. FixtureID=[{}]", fixtureId);
        }
        if (result.size() > 1){
            log.error("More than one fixture received from getFixtureById request! Results=[{}]", result);
        }
        return result.get(0);
    }

    public List<FixtureResponse> sendFixturesRequest(HttpRequest request){

        var response = sendRequest(request);

        try{
            var fixturesJson = response.getObject().getJSONArray("response");

            List<FixtureResponse> fixtures = new ArrayList<>();
            for (int i = 0; i < fixturesJson.length(); i++){
                var result = convert(fixturesJson.getJSONObject(i), FixtureResponse.class);
                fixtures.add(result);
            }

            return fixtures;
        }
        catch (JSONException ex){
            log.error("Error parsing Fixtures response from API Football service. Response=[{}] ", response.toString());
            throw new RuntimeException(ex);
        }
    }

    public List<Event> getEvents(Long fixtureId){
        var request = buildRequest(EVENTS_PATH)
            .queryString(FIXTURE_ID, fixtureId);

        log.info("Sending getEvents request to API Football service. FixtureID=[{}]", fixtureId);

        var response = sendRequest(request);

        try{
            var eventsJson = response.getObject().getJSONArray("response");
            List<Event> events = new ArrayList<>();
            for (int i = 0; i < eventsJson.length(); i++){
                var result = convert(eventsJson.getJSONObject(i), Event.class);
                events.add(result);
            }
            return events;
        }
        catch (JSONException ex){
            log.error("Error parsing Events response from API Football service. Response=[{}] ", response.toString());
            throw new RuntimeException(ex);
        }
    }

    @Cacheable("api-football-players")
    public Player getPlayer(Long playerId, Integer season){
        var request = buildRequest(PLAYERS_PATH)
            .queryString(PLAYER_ID, playerId)
            .queryString(SEASON, season);

        log.info("Sending getPlayer request to API Football service. PlayerID=[{}] Season=[{}]", playerId, season);

        var response = sendRequest(request);

        try {
            var playerJson = response.getObject().getJSONArray("response");
            if (playerJson.length() != 1){
                log.error("More than one player recieved in player response! Response=[{}]", response.toString());
            }
            var playerObject = playerJson.getJSONObject(0);
            if (playerObject.has("player")){
                return convert(playerObject.getJSONObject("player"), Player.class);
            }
            else {
                log.error("Unexpected Player response. Response=[{}]", response.toString());
                throw new RuntimeException("Unexpected Player response.");
            }
        }
        catch (JSONException ex){
            log.error("Error parsing Player response from API Football service. Response=[{}] ", response.toString());
            throw new RuntimeException(ex);
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
                log.error("Error in API-Football response. Error=[{}}", errorsArray.toString());
                throw new RuntimeException("Error in API-Football response.");
            }
        }
        catch (JSONException ex){
            //do nothing because this means there are no errors
        }
        try {
            JSONObject errors = responseJson.getObject().getJSONObject("errors");
            if (!errors.isEmpty()){
                log.error("Error in API-Football response. Error=[{}}", errors.toString());
                throw new RuntimeException("Error in API-Football response.");
            }
        }
        catch (JSONException ex){
            //do nothing because this is expected in 99% of cases
        }

        return responseJson;
    }

    private <T> T convert(JSONObject json, Class<? extends T> clazz){
        return gson.fromJson(json.toString(), clazz);
    }

}
