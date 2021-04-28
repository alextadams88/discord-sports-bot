package com.deadbeef.discordsportsbot.listeners;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConfigurationUpdateListener extends MessageListener implements EventListener<MessageCreateEvent> {

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    private Predicate<MessageCreateEvent> hasAdminPermissionPredicate =
        messageCreateEvent ->
            messageCreateEvent.getMember()
                .filter(member ->
                    member.getBasePermissions()
                        .filter(permissions ->
                            permissions.contains(Permission.ADMINISTRATOR))
                        .blockOptional()
                        .isPresent())
                .isPresent();

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.just(event)
            .filter(e -> e.getMessage().getAuthor().map(user -> !user.isBot()).orElse(false))
            .filter(e -> event.getMessage().getContent().startsWith("!"))
            .filter(hasAdminPermissionPredicate)
            .flatMap(e -> processConfigurationUpdateCommand(e.getMessage()))
            .then();
    }

    private Mono<Void> processConfigurationUpdateCommand(Message eventMessage) {
        //todo: hook into the database here and actually update the configuration
        return Mono.empty();
    }

}
