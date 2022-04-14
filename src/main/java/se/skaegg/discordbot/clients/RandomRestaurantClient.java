package se.skaegg.discordbot.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.http.client.HttpClient;
import se.skaegg.discordbot.dto.Restaurant;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RandomRestaurantClient {


    private String token;
    private String restaurantUrl;

    private static final Logger log = LoggerFactory.getLogger(RandomRestaurantClient.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public RandomRestaurantClient(String token, String restaurantUrl) {
        this.token = token;
        this.restaurantUrl = restaurantUrl;
    }


    public List<Restaurant> process(String searchWord, int numberOfResults) {
        // URL encode the city name
        String searchEncoded = URLEncoder.encode(searchWord, StandardCharsets.UTF_8);
        // If no extra parameter is added to the command nothing will be added to the URL, if a city is added as a parameter that will be added in the end of the URL
        final String url = restaurantUrl +
                "/" +
                searchEncoded +
                "?" +
                "count=" +
                numberOfResults;

        final String response = HttpClient.create()
                .headers(h -> h.set("lunch-secret", token))
                .get()
                .uri(url)
                .responseContent()
                .aggregate()
                .asString()
                .block();

        log.debug(response);

        List<Restaurant> restaurantsResult = new ArrayList<>();
        try {
            restaurantsResult = MAPPER.readValue(response, new TypeReference<>() {});
        }
        catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error(String.format("Couldn't map the json string to Restaruant DTO %n%s %n%s", e.getMessage(), Arrays.toString(e.getStackTrace())));
        }

        return restaurantsResult;
    }


    public List<Restaurant> process() {
        return process("Norrtälje", 1);
    }


    public List<Restaurant> process(String searchWord) {
        return process(searchWord, 1);
    }
}
