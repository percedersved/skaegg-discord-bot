package se.skaegg.discordbot.handlers;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;
import se.skaegg.discordbot.clients.RandomRestaurantClient;
import se.skaegg.discordbot.dto.Restaurant;

import java.util.List;

public class RestaurantDuel {

    String token;
    String restaurantUrl;
    public RestaurantDuel(String token, String restaurantUrl) {
        this.token = token;
        this.restaurantUrl = restaurantUrl;
    }

    public Mono<Void> process(Message eventMessage) {

        String searchFormatted = eventMessage.getContent().replaceAll("(?i)!restaurangduell\\s*", "");
        String whiteSpace = "\u200B";

        List<Restaurant> restaurantList;
        if (searchFormatted.isBlank()) {
            restaurantList = new RandomRestaurantClient(token, restaurantUrl).process("Norrtälje", 2);
        } else {
            restaurantList = new RandomRestaurantClient(token, restaurantUrl).process(searchFormatted, 2);
        }

        Restaurant restaurantOne = restaurantList.get(0);
        Restaurant restaurantTwo = restaurantList.get(1);

        String leftColumnEmbedROne =
                restaurantOne.getRating() + "\n" +
                restaurantOne.getOpeningHours() + "\n" +
                restaurantOne.getPricing();

        String rightColumnEmbedROne =
                restaurantOne.getWebsite() + "\n" +
                restaurantOne.getAddress() + "\n" +
                restaurantOne.getPhone();

        String footerEmbedROne =
                "\u200B\n" +
                restaurantOne.getReviewText() + "\n" +
                restaurantOne.getReviewByline();

        EmbedCreateSpec embedROne = EmbedCreateSpec.builder()
                .color(Color.of(221, 46, 68))
                .title(":red_circle: " + restaurantOne.getName())
                .url(restaurantOne.getUrl())
                .addField(whiteSpace, leftColumnEmbedROne, true)
                .addField(whiteSpace, rightColumnEmbedROne, true)
                .footer(footerEmbedROne, "")
                .build();


        String leftColumnEmbedRTwo =
                restaurantTwo.getRating() + "\n" +
                restaurantTwo.getOpeningHours() + "\n" +
                restaurantTwo.getPricing();

        String rightColumnEmbedRTwo =
                restaurantTwo.getWebsite() + "\n" +
                restaurantTwo.getAddress() + "\n" +
                restaurantTwo.getPhone();

        String footerEmbedRTwo =
                "\u200B\n" +
                restaurantTwo.getReviewText() + "\n" +
                restaurantTwo.getReviewByline();

        EmbedCreateSpec embedRTwo = EmbedCreateSpec.builder()
                .color(Color.of(85, 172, 238))
                .title(":blue_circle: " + restaurantTwo.getName())
                .url(restaurantTwo.getUrl())
                .addField(whiteSpace, leftColumnEmbedRTwo, true)
                .addField(whiteSpace, rightColumnEmbedRTwo, true)
                .footer(footerEmbedRTwo, "")
                .build();


        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(MessageCreateSpec.builder()
                        .content("**Var vill du äta? :crossed_swords:**")
                        .addEmbed(embedROne)
                        .addEmbed(embedRTwo)
                        .build())
                        .flatMap(msg -> msg.addReaction(ReactionEmoji.unicode("🔴"))
                                .then(msg.addReaction(ReactionEmoji.unicode("\uD83D\uDD35")))))
                .then();
    }
}
