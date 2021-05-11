package com.deadbeef.discordsportsbot.service.discord;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.service.ChannelService;
import discord4j.rest.service.GuildService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscordClientService {

    private final GatewayDiscordClient discordClient;
    private final GuildService guildService;
    private final ChannelService channelService;

    @Cacheable("discord-message-channel")
    public MessageChannel getMessageChannel(Long channelId){
        return discordClient.getChannelById(Snowflake.of(channelId)).ofType(MessageChannel.class).block();
    }

}
