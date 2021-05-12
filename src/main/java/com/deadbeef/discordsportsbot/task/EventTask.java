package com.deadbeef.discordsportsbot.task;

import com.deadbeef.discordsportsbot.domain.apifootball.Event;
import com.deadbeef.discordsportsbot.domain.apifootball.Fixture;
import com.deadbeef.discordsportsbot.domain.apifootball.FixtureResponse;
import com.deadbeef.discordsportsbot.external.apifootball.ApiFootballService;
import com.deadbeef.discordsportsbot.service.DiscordMessageService;
import discord4j.common.util.Snowflake;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@RequiredArgsConstructor
@Slf4j
public class EventTask implements Runnable{

    private final ApiFootballService footballService;
    private final DiscordMessageService discordMessageService;
    private FixtureResponse fixture;
    private List<Event> events = new ArrayList<>();
    private Map<Event, Snowflake> eventToMessageMap = new HashMap<>();

    @Override
    public void run() {
        if (Objects.isNull(fixture) || Objects.isNull(fixture.getFixture())){
            throw new RuntimeException("Attempting to run Fixture Event with no fixture.");
        }
        while (!gameIsOver(fixture.getFixture())){
            var newEvents = footballService.getEvents(fixture.getFixture().getId());
            var updatedFixture = footballService.getFixtureById(fixture.getFixture().getId());
            if (updatedFixture != null){
                fixture = updatedFixture;
            }
            //Need better logic here to also check for events that have been removed, such as goals called back by VAR
            //I need a way to figure out if I need to update previous events. The Events in this API don't have unique IDs so no way to uniquely identify them.
            //For now I'll just have to assume that Events will always come in totally complete, and never get updated. If this is not the case then something will have to change.
            //I'll find out when I do a live test this weekend.
            newEvents.stream().filter(events::contains).forEach(event -> {
                log.info("Emitting new event. Event=[{}]", event);
                discordMessageService.emitEvent(event, fixture, eventToMessageMap);
            });
            events.stream().filter(newEvents::contains).forEach(removedEvent -> {
                log.info("Removing event, it is not present in updated Events list. Event=[{}]", removedEvent);
                if (eventToMessageMap.containsKey(removedEvent)){
                    discordMessageService.removeEventDueToVAR(eventToMessageMap.get(removedEvent));
                }
            });
            events = newEvents;
            try {
                Thread.sleep(30 * 1000);
            }
            catch (InterruptedException ex){
                log.error("Thread interrupted. Halting thread.", ex);
                return;
            }
        }

    }

    private boolean gameIsOver(Fixture fixture){
        //TODO: we need better logic here to handle special cases, such as:
        //1. when halftime starts, give the endpoint a 15 minute break
        //2. if the match is Postponed or Suspended, then we need to figure out what to do there
        switch (fixture.getStatus().getShortStatus()){
            case "TBD":
            case "NS":
            case "1H":
            case "HT":
            case "2H":
            case "ET":
            case "P":
            case "BT":
                return false;
            case "FT":
            case "AET":
            case "PEN":
            case "SUSP":
            case "INT":
            case "PST":
            case "CANC":
            case "ABD":
            case "AWD":
            case "WO":
            default:
                return true;

        }
    }
}
