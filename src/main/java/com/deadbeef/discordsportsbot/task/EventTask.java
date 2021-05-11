package com.deadbeef.discordsportsbot.task;

import com.deadbeef.discordsportsbot.domain.apifootball.Event;
import com.deadbeef.discordsportsbot.domain.apifootball.FixtureResponse;
import com.deadbeef.discordsportsbot.external.apifootball.ApiFootballService;
import com.deadbeef.discordsportsbot.service.DiscordMessageService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EventTask implements Runnable{

    private final ApiFootballService footballService;
    private final DiscordMessageService discordMessageService;
    private FixtureResponse fixture;
    private List<Event> events = new ArrayList<>();

    @Override
    public void run() {
        //TODO: poll the API and get the game
        if (Objects.isNull(fixture)){
            throw new RuntimeException("Attempting to run Fixture Event with no fixture.");
        }
        var events = footballService.getEvents(fixture.getFixture().getId());
        events.forEach(event -> {
            discordMessageService.emitEvent(event, fixture);
        });
    }
}
