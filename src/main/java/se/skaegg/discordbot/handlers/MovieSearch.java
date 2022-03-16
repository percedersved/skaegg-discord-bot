package se.skaegg.discordbot.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import se.skaegg.discordbot.dto.OmdbTitleSearchResultDTO;

import java.net.URLEncoder;
import java.nio.charset.Charset;

public class MovieSearch {

    String apiToken;

    private static final ObjectMapper MAPPER = new ObjectMapper();


    public MovieSearch(String apiToken) {
        this.apiToken=apiToken;
    }


    public Mono<Void> process(Message eventMessage) throws JsonProcessingException {

        String searchFormatted = eventMessage.getContent().replaceAll("(?i)!film ", "");

        String queryParams = apiToken + "&t=" + URLEncoder.encode(searchFormatted, Charset.defaultCharset());

        String response = HttpClient.create()
            .get()
            .uri("http://www.omdbapi.com/?plot=short&apikey=" + queryParams)
            .responseContent()
            .aggregate()
            .asString()
            .block();

        OmdbTitleSearchResultDTO omdbResult = MAPPER.readValue(response, new TypeReference<>() {});

        String successfull = omdbResult.getResponse();
        String title = omdbResult.getTitle();
        String description = omdbResult.getPlot();
        String actors = omdbResult.getActors();
        String genre = omdbResult.getGenre();
        String released = omdbResult.getReleased();
        String imdbRating = omdbResult.getImdbRating();
        String awards = omdbResult.getAwards();
        String imageUrl = omdbResult.getPoster();
        String totalSeasons = omdbResult.getTotalSeasons() == null ? "" : "\n** *Antal säsonger:** " + omdbResult.getTotalSeasons();

        String otherInfo = "** * Genre:** " + genre +
                "\n** * Skådisar:** " + actors +
                "\n** * Släpptes:** " + released +
                "\n** * IMDB rating:** " + imdbRating +
                "\n** * Priser:** " + awards +
                totalSeasons;

        String imdbLink = "https://www.imdb.com/title/" + omdbResult.getImdbID();

        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                    if (successfull.equals("False"))
                    {
                        return channel.createMessage("Din sökning gav ingen träff. No movie for you! <:koerdittjaeklaboegrace:814187249288872016>");
                    }
                    else {
                        return channel.createEmbed(spec ->
                                spec.setColor(Color.of(90, 130, 180))
                                        .setTitle(title)
                                        .setImage(imageUrl.equals("N/A") ? "" : imageUrl)
                                        .addField("Handling", description, true)
                                        .addField("Övrigt", otherInfo, true)
                                        .setUrl(imdbLink));
                    }
                })
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage("Ooops nu gick något fel, Testa igen vettja!")))
                .then();
    }
}
