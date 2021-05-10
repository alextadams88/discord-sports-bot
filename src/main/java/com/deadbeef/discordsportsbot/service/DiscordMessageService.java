package com.deadbeef.discordsportsbot.service;

import com.deadbeef.discordsportsbot.domain.apifootball.Event;
import com.deadbeef.discordsportsbot.domain.apifootball.Fixture;
import com.deadbeef.discordsportsbot.domain.apifootball.FixtureResponse;
import com.deadbeef.discordsportsbot.service.discord.EmbedService;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.service.ChannelService;
import discord4j.rest.service.GuildService;
import java.util.List;
import java.util.Objects;
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
        var channel = discordClient.getChannelById(Snowflake.of(channelId)).ofType(MessageChannel.class).block();
        events.forEach(event -> {
            var embed = channel.createEmbed(spec -> embedService.createEventEmbed(spec, event, fixture));
            var message = embed.block();
            log.info(message.toString());
        });
    }
}
