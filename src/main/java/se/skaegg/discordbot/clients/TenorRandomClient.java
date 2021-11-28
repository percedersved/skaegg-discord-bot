package se.skaegg.discordbot.clients;

import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import reactor.netty.http.client.HttpClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;

public class TenorRandomClient
{
    @Value("${tenor.api.token}")
    String token;

    private static final Logger LOGGER = LoggerFactory.getLogger(TenorRandomClient.class);

    public String process(final String searchWord) {

        final String tenorURI = "https://g.tenor.com/v1/random?q=" + searchWord + "&key=" + token;

        final String response = HttpClient.create()
                .get()
                .uri(tenorURI)
                .responseContent()
                .aggregate()
                .asString()
                .block();

        final JSONObject responseJson = new JSONObject(response);
        final JSONArray results = responseJson.getJSONArray("results");
        final JSONObject resultsZero = results.getJSONObject(0);
        final JSONArray media = resultsZero.getJSONArray("media");
        final JSONObject mediaZero = media.getJSONObject(0);
        final JSONObject gif = mediaZero.getJSONObject("gif");
        final String gifUrl = gif.getString("url");
        return gifUrl;
    }
}
