package com.deadbeef.discordsportsbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DiscordSportsBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscordSportsBotApplication.class, args);
    }

}
