package se.skaegg.discordbot.handlers;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BolagetOpeningHours implements EventHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(BolagetOpeningHours.class);

    String storeId;
    String token;

    public BolagetOpeningHours (String storeId, String token) {
        this.storeId = storeId;
        this.token = token;
    }

    @Override
    public Mono<Void> process(Message eventMessage) {


        //Create the http client and call Systembolaget API
        String response = HttpClient.create()
                .headers(h -> h.set("Ocp-Apim-Subscription-Key", token))
                .get()
                .uri("https://api-extern.systembolaget.se/site/V2/Store/" + storeId)
                .responseContent()
                .aggregate()
                .asString()
                .block();

        JSONObject responseJson = new JSONObject(response);
        JSONArray openingHoursArray = responseJson.getJSONArray("openingHours");

        StringBuilder sb = new StringBuilder();

        //Loop through days in the openinghours json array
        for (int i = 0; i < 5; i ++) {

            String openFrom = openingHoursArray.getJSONObject(i).getString("openFrom").substring(0, 5);
            String openTo = openingHoursArray.getJSONObject(i).getString("openTo").substring(0, 5);
            String dateTime = openingHoursArray.getJSONObject(i).getString("date");

            String formattedDate = formatDate(dateTime);

            if (isToday(dateTime)) {
                sb.append("Idag\n");
            }
            else {
                sb.append(formattedDate);
                sb.append("\n");
            }

            if (openFrom.equals("00:00")) {
                sb.append("Stängt\n\n");
            }
            else {
                sb.append(openFrom);
                sb.append(" - ");
                sb.append(openTo);
                sb.append("\n\n");
            }
        }

        String openingHoursString = sb.toString();

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .color(Color.of(90, 130, 180))
                .title("Öppettider för Bollis i Norrtälje :beers:")
                .description(openingHoursString)
                .build();


        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(embed)
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage("Äsch, nu kraschade nåt, Testa igen vettja!")))
                .then());

    }


    private boolean isToday(String date) {

        Date d = null;
        Date now = null;
        try {
            d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);
            now = new Date(System.currentTimeMillis());
        }
        catch (ParseException e) {
            LOGGER.error(e.getMessage());
        }

        if(d != null) {
            return DateUtils.isSameDay(d, now);
        }
        else {
            return false;
        }
    }


    private String formatDate(String date) {
        String formattedDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.forLanguageTag("sv"));
            Date d = sdf.parse(date);
            sdf.applyPattern("EEEE, dd MMMM");
            formattedDate = sdf.format(d);
            formattedDate = formattedDate.substring(0, 1).toUpperCase() +
                    formattedDate.substring(1);
        }
        catch (ParseException e) {
            LOGGER.error("Failed to parse date from Systembolaget API\n{}", e.getMessage());
        }

        return formattedDate;
    }
}
