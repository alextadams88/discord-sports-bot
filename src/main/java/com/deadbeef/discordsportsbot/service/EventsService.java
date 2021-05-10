package com.deadbeef.discordsportsbot.service;

import com.deadbeef.discordsportsbot.domain.apifootball.Event;
import com.deadbeef.discordsportsbot.external.apifootball.ApiFootballService;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventsService {

    private final ApiFootballService apiFootballService;

    public List<Event> fetchEvents(Long fixtureId, Integer season){
        List<Event> events = apiFootballService.getEvents(fixtureId);
        events.forEach(event -> {
            if (event.getPlayer() != null && event.getPlayer().getId() != null){
                var player = apiFootballService.getPlayer(event.getPlayer().getId(), season);
                if (Objects.nonNull(player)){
                    event.setPlayer(player);
                }
            }
            if (event.getAssist() != null && event.getAssist().getId() != null){
                var assister = apiFootballService.getPlayer(event.getAssist().getId(), season);
                if (Objects.nonNull(assister)){
                    event.setAssist(assister);
                }
            }
        });
        return events;
    }

}
