package com.deadbeef.discordsportsbot.listeners;

import discord4j.core.object.entity.Message;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class MessageListener {

    private final static String mentionsRegex = "\\<\\@\\!.*?\\>";

    public Mono<Void> processCommand(Message eventMessage) {
        log.info(eventMessage.getContent());
        var mentions = Pattern.compile(mentionsRegex)
            .matcher(eventMessage.getContent())
            .results()
            .map(MatchResult::group)
            .collect(Collectors.toList());
        var content = mentions.stream().map(name -> name + " is a fucko!\n").collect(Collectors.joining());
        return Mono.just(eventMessage)
            .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
            .filter(a -> !ObjectUtils.isEmpty(content))
//            .filter(message -> message.getAuthor().map(user -> user.getUsername().contains("deadbeef")).orElse(false))
            .filter(message -> message.getContent().startsWith("!"))
            .flatMap(Message::getChannel)
            .flatMap(channel -> channel.createMessage(content))
            .then();
    }

}
