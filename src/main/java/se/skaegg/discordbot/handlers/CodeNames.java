package se.skaegg.discordbot.handlers;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CodeNames implements EventHandler{

    //Taken from swear word generator https://www.fantasynamegenerators.com/swear-words.php
    private static final List<String> PATHS = Arrays.asList(
            "oh_coconuts", "slip_and_slide", "fairydust", "oblivious_ogre", "fire_and_brimstone", "storm_and_thunder", "blasted_burrito",
            "thunder_and_lightning", "mother_father", "blangdang", "doubleheaded_nimwit", "turd_in_a_suit", "eternal_oblivion", "sand_crackers",
            "blazing_inferno", "death_and_taxes", "ignorant_ogre", "hogwash", "weenie_in_a_beanie", "shut_the_front_door", "burned_gravy",
            "oh_patches", "balderdash", "swizzle_sticks", "monkey_disco", "whack_a_holy_moly", "burps_and_farts", "birdbrained_bandit"
    );
    private static final int SIZE = PATHS.size();
    private static final Random RANDOM = new Random();

    @Override
    public Mono<Void> process(Message eventMessage) {

        String codeNamesUrl = "https://horsepaste.com/" + PATHS.get(RANDOM.nextInt(SIZE));

        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(codeNamesUrl))
                .then();
    }
}
