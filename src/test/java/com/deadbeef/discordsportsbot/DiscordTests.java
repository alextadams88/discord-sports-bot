package com.deadbeef.discordsportsbot;

import com.deadbeef.discordsportsbot.service.DiscordMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DiscordTests {

    @Autowired
    private DiscordMessageService discordMessageService;

    @Test
    public void testGetGuilds(){
        discordMessageService.getGuilds();
    }

}
