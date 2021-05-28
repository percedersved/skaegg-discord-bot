package se.skaegg.discordbot.handlers;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface EventHandler {

    public Mono<Void> process(Message eventMessage);


}
