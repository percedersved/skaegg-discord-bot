package se.skaegg.discordbot.handlers;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class Ping implements EventHandler{


    @Override
    public Mono<Void> process(Message eventMessage) {

        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Pong!"))
                .then();
    }
}
