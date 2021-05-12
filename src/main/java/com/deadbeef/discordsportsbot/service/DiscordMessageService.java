package com.deadbeef.discordsportsbot.service;

import com.deadbeef.discordsportsbot.domain.apifootball.Event;
import com.deadbeef.discordsportsbot.domain.apifootball.Fixture;
import com.deadbeef.discordsportsbot.domain.apifootball.FixtureResponse;
import com.deadbeef.discordsportsbot.service.discord.DiscordClientService;
import com.deadbeef.discordsportsbot.service.discord.EmbedService;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.service.ChannelService;
import discord4j.rest.service.GuildService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscordMessageService {

    private final Long channelId = 706877055995871323L; //mental hill secret channel
//    private final Long channelId = 745125184973045862L; //johnny rockets general

    private final GatewayDiscordClient discordClient;
    private final GuildService guildService;
    private final ChannelService channelService;
    private final EmbedService embedService;
    private final DiscordClientService discordClientService;

    public void getGuilds(){
//        var channels = guildService.getGuildChannels(745125184541032520L); //johnny rockets
//        channels.doOnEach(channel -> {
//            var myChannel = channel.get();
//            if (Objects.nonNull(myChannel)) {
//                log.info(myChannel.toString());
//            }
//        }).blockLast();

//        var guilds = discordClient.rest().getGuilds().doOnEach(guild -> {
//            var myGuild = guild.get();
//            if (Objects.nonNull(myGuild)) {
//                log.info(myGuild.toString());
//                }
//            }
//        ).blockLast();
    }

    //TODO: we need to not fetch the channel every time. We need to build a cache to cache the channels, but for now just pass all the events at once
    public void emitEvents(List<Event> events, FixtureResponse fixture){
        events.forEach(event -> {
            emitEvent(event, fixture, new HashMap<>());
        });
    }

    public void emitEvent(Event event, FixtureResponse fixtureResponse, Map<Event, Snowflake> eventSnowflakeMap){
        discordClient.getChannelById(Snowflake.of(channelId)).ofType(MessageChannel.class)
            .flatMap(channel -> channel.createEmbed(spec -> embedService.createEventEmbed(spec, event, fixtureResponse)))
            .subscribe(completion -> {
                log.info("Successfully sent Event=[{}] to channel=[{}]", event, channelId);
                log.info("Reponse message=[{}]", completion);
                eventSnowflakeMap.put(event, completion.getId());
            }, error -> {
                log.info("Unable to send Event=[{}] to channel=[{}]", event, channelId);
                log.info("", error);
            });
    }

    //TODO: figure out a way to do this, this may actually not make sense with the current API-FOOTBALL contract
    public Snowflake updateEvent(Event event, FixtureResponse fixtureResponse, Snowflake messageId){
        return null;
    }

    public void removeEventDueToVAR(Snowflake messageId) {
        discordClient.getChannelById(Snowflake.of(channelId)).ofType(MessageChannel.class)
            .flatMap(channel -> channel.getMessageById(messageId))
            .flatMap(Message::delete)
            .subscribe(completion -> {
                    log.error("Deleted message ID=[{}] in channel ID=[{}]", messageId, channelId);
                },
                error -> {
                    log.error("Unable to delete message ID=[{}] in channel ID=[{}]", messageId, channelId);
                    log.error("", error);
                });
    }
}
