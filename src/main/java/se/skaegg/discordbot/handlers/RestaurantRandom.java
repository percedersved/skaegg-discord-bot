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
                restaurant.getRating() + "\n" +
                restaurant.getOpeningHours() + "\n" +
                restaurant.getPricing();
        // Check the column only contains whitespace, then add the special whitespace char to avoid crash
        leftColumnEmbed = leftColumnEmbed.isBlank() ? whiteSpace : leftColumnEmbed;

        String rightColumnEmbed =
                restaurant.getWebsite() + "\n" +
                restaurant.getAddress() + "\n" +
                restaurant.getPhone();
        // Check the column only contains whitespace, then add the special whitespace char to avoid crash
        rightColumnEmbed = rightColumnEmbed.isBlank() ? whiteSpace : rightColumnEmbed;

        String footerEmbed =
                "\u200B\n" +
                restaurant.getReviewText() + "\n" +
                restaurant.getReviewByline();


        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .color(Color.of(90, 130, 180))
                .title(restaurant.getName())
                .url(restaurant.getUrl())
                .addField("Information", leftColumnEmbed, true)
                .addField("Kontakt", rightColumnEmbed, true)
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
