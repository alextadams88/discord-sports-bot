package com.deadbeef.discordsportsbot.service.discord;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.service.ChannelService;
import discord4j.rest.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscordClientService {

    private final GatewayDiscordClient discordClient;
    private final GuildService guildService;
    private final ChannelService channelService;

    //according to a guy in the Discord server for Discord4J, discord4j handles the caching for you, so no need to cache this
//    @Cacheable("discord-message-channel")
    public MessageChannel getMessageChannel(Long channelId){
        return discordClient.getChannelById(Snowflake.of(channelId)).ofType(MessageChannel.class).block();
    }

}
