package com.deadbeef.discordsportsbot;

import com.deadbeef.discordsportsbot.external.apifootball.ApiFootballService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiscordSportsBotApplicationTests {

    @Autowired
    ApiFootballService apiFootballService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testGetFixtures(){
        //mls
        apiFootballService.getFixtures(LocalDate.now(), 253L, 2021L);
    }

    @Test
    public void testGetEvents(){
        apiFootballService.getEvents(695181L);
    }

}
