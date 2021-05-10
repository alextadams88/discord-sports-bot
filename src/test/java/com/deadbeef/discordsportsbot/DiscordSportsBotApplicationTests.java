package com.deadbeef.discordsportsbot;

import com.deadbeef.discordsportsbot.external.apifootball.ApiFootballService;
import com.deadbeef.discordsportsbot.service.DiscordMessageService;
import com.deadbeef.discordsportsbot.service.EventsService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiscordSportsBotApplicationTests {

    @Autowired
    ApiFootballService apiFootballService;

    @Autowired
    DiscordMessageService discordMessageService;

    @Autowired
    EventsService eventsService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testGetFixtures(){
        //mls
        apiFootballService.getFixtures(LocalDate.now().minusDays(1), 253L, 2021);
    }

    @Test
    public void testGetEvents(){
        apiFootballService.getEvents(695181L);
    }

    @Test
    public void testSendEvents() {
        var fixtures = apiFootballService.getFixtures(LocalDate.now().minusDays(1), 253L, 2021);
        var fixture = fixtures.stream().filter(fixtureResponse -> fixtureResponse.getFixture().getId().equals(695187L)).findFirst().get();
        var events = eventsService.fetchEvents(695187L, 2021);
        discordMessageService.emitEvents(events, fixture);
    }
}
