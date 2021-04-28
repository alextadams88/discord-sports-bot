package com.deadbeef.discordsportsbot.config;

import com.deadbeef.discordsportsbot.listeners.EventListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordGatewayConfig {

    @Value("${discord.api-token}")
    private String apiToken;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
        var client = DiscordClientBuilder.create(apiToken)
            .build()
            .login()
            .block();

        if (Objects.nonNull(eventListeners)) {
            eventListeners.forEach(listener -> client.on(listener.getEventType()).flatMap(listener::execute).onErrorResume(listener::handleError).subscribe());
        }

        return client;
    }

}
