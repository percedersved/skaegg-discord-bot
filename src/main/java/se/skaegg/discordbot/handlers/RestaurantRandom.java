package se.skaegg.discordbot.handlers;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;
import se.skaegg.discordbot.clients.RandomRestaurantClient;
import se.skaegg.discordbot.dto.Restaurant;

import java.util.List;

public class RestaurantRandom {

    String token;
    String restaurantUrl;

    public RestaurantRandom(String token, String restaurantUrl) {
        this.token = token;
        this.restaurantUrl = restaurantUrl;
    }

    public Mono<Void> process(Message eventMessage) {

        String searchFormatted = eventMessage.getContent().replaceAll("(?i)!restaurang\\s*", "");
        String whiteSpace = "\u200B";

        List<Restaurant> restaurantList;
        if (searchFormatted.isBlank()) {
            restaurantList = new RandomRestaurantClient(token, restaurantUrl).process();

        }
        else {
            restaurantList = new RandomRestaurantClient(token, restaurantUrl).process(searchFormatted);
        }

        Restaurant restaurant = restaurantList.get(0);

        String leftColumnEmbed =
                restaurant.getRating() + whiteSpace + "\n" + // The \u200b whitespace is needed because otherwise it will crash if all of these are "" and the embed-field will be empty
                restaurant.getOpeningHours() + "\n" +
                restaurant.getPricing();

        String rightColumnEmbed =
                restaurant.getWebsite() + whiteSpace + "\n" +
                restaurant.getAddress() + "\n" +
                restaurant.getPhone();

        String footerEmbed =
                "\u200B\n" +
                restaurant.getReviewText() + "\n" +
                restaurant.getReviewByline();


        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .color(Color.of(90, 130, 180))
                .title(restaurant.getName())
                .url(restaurant.getUrl())
                .addField(whiteSpace, leftColumnEmbed, true)
                .addField(whiteSpace, rightColumnEmbed, true)
                .image(restaurant.getPhoto())
                .footer(footerEmbed, "")
                .build();


        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(MessageCreateSpec.builder()
                                .content("**Du/Ni borde verkligen testa:**")
                                .addEmbed(embed)
                                .build()))
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage("Tyvärr, nåt gick fel.")))
                .then();
    }
}
