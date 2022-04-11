package se.skaegg.discordbot.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import se.skaegg.discordbot.dto.Restaurant;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Lunch {

    String token;

    String restaurantUrl;

    private static final Logger log = LoggerFactory.getLogger(Lunch.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Lunch(String token, String restaurantUrl) {
        this.token = token;
        this.restaurantUrl = restaurantUrl;
    }

    public Mono<Void> process(Message eventMessage) {

        String searchFormatted = eventMessage.getContent().replaceAll("(?i)!lunchtips\\s*", "");
        // URL encode the city name
        String searchEncoded = URLEncoder.encode(searchFormatted, StandardCharsets.UTF_8);
        // If no extra parameter is added to the command nothing will be added to the URL, if a city is added as a parameter that will be added in the end of the URL
        final String url = searchFormatted.isBlank() ? restaurantUrl : restaurantUrl + "/" + searchEncoded;

        final String response = HttpClient.create()
                .headers(h -> h.set("lunch-secret", token))
                .get()
                .uri(url)
                .responseContent()
                .aggregate()
                .asString()
                .block();

        List<Restaurant> restaurantsResult;
        try {
            restaurantsResult = MAPPER.readValue(response, new TypeReference<>() {});
        }
        catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Couldn't map the json string to Restaruant DTO");
            return Mono.empty();
        }

        Restaurant restaurant = restaurantsResult.get(0);

        StringBuilder sb = new StringBuilder();
        sb.append("Betyg: ");
        sb.append(restaurant.getRating());
        sb.append("\n");
        sb.append("Adress: ");
        sb.append(restaurant.getAddress());
        sb.append("\n\n");
        String restaurantDescription = sb.toString();

        String photoUrl = restaurant.getPhoto() != null ? restaurant.getPhoto() : "";

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .color(Color.of(90, 130, 180))
                .title(restaurant.getName())
                .url(restaurant.getUrl())
                .image(photoUrl)
                .description(restaurantDescription)
                .build();

        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(embed))
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage("Tyvärr, nåt gick fel.")))
                .then();


    }
}
