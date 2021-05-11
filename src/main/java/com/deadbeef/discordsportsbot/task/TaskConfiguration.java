package com.deadbeef.discordsportsbot.task;

import com.deadbeef.discordsportsbot.external.apifootball.ApiFootballService;
import com.deadbeef.discordsportsbot.service.DiscordMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@RequiredArgsConstructor
public class TaskConfiguration {

    @Bean("fixturePollingEvent")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public EventTask getFixturePollingEvent(ApiFootballService apiFootballService, DiscordMessageService discordMessageService){
        return new EventTask(apiFootballService, discordMessageService);
    }

}
