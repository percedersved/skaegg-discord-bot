package se.skaegg.discordbot.configuration;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.channel.Channel;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import se.skaegg.discordbot.events.EventListener;
import se.skaegg.discordbot.jpa.TimerEntity;
import se.skaegg.discordbot.jpa.TimerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class BotConfiguration {

    private static final Logger log = LoggerFactory.getLogger( BotConfiguration.class );

    @Value("${token}")
    private String token;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
        GatewayDiscordClient client = null;

        try {
            client = DiscordClientBuilder.create(token)
                    .build()
                    .gateway()
                    .setEnabledIntents(IntentSet.of(
                            Intent.GUILD_MEMBERS,
                            Intent.GUILD_PRESENCES,
                            Intent.GUILD_VOICE_STATES,
                            Intent.GUILD_MESSAGES,
                            Intent.GUILD_MESSAGE_TYPING,
                            Intent.GUILDS
                    ))
                    .login()
                    .block();

            log.info("Client created and logged in. Continuing with listeners");

            for(EventListener<T> listener : eventListeners) {
                client.on(listener.getEventType())
                        .flatMap(listener::execute)
                        .onErrorResume(listener::handleError)
                        .subscribe();
                log.info(listener + "Registered");
            }


        }
        catch ( Exception exception ) {
            log.error( "Be sure to use a valid bot token! Exception: {}", exception.getMessage() );
        }


        return client;
    }
}