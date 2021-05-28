package se.skaegg.discordbot.events;

import discord4j.core.event.domain.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import se.skaegg.discordbot.handlers.CodeNames;
import se.skaegg.discordbot.handlers.Ping;
import se.skaegg.discordbot.handlers.Teams;
import se.skaegg.discordbot.handlers.TeamsVoice;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


@Service
public class MessageListener implements EventListener<MessageCreateEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }


    @Override
    public Mono<Void> execute(MessageCreateEvent event) {

        Map<String, Supplier<Mono<Void>>> commands = new HashMap<>();
        commands.put("ping", () -> new Ping().process(event.getMessage()));
        commands.put("lag", () -> new Teams().process(event.getMessage()));
        commands.put("lagvoice", () -> new TeamsVoice().process(event.getMessage()));
        commands.put("codenames", () -> new CodeNames().process(event.getMessage()));
//        commands.put("bolaget", () -> new Bolaget().process(event.getMessage()));

        String lowerKeyEvent = event.getMessage().getContent().toLowerCase().replace("!", "");

        if (lowerKeyEvent.contains(" ")) {
            int spaceIndex = lowerKeyEvent.indexOf(" ");
            lowerKeyEvent = lowerKeyEvent.substring(0, spaceIndex);
        }

        if (commands.containsKey(lowerKeyEvent)) {
            LOGGER.debug("se.skaegg.discordbot.events.MessageListener -> User: {} triggered command {}", event.getMember().get().getDisplayName(), lowerKeyEvent);
            return commands.get(lowerKeyEvent).get();
        }

        return Mono.empty();
    }
}
