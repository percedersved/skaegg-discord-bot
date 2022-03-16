package se.skaegg.discordbot.handlers;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Teams implements EventHandler{
    @Override
    public Mono<Void> process(Message eventMessage) {

        List<String> teamsList = setUpTeams(eventMessage.getContent());
        String teamRed = teamsList.get(0);
        String teamBlue= teamsList.get(1);

        final var IMAGE_URL_TEAMS = "https://static-cdn.jtvnw.net/emoticons/v1/166266/3.0";

        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel ->
                        channel.createEmbed(spec ->
                                spec.setColor(Color.of(90, 130, 180))
                                    .setImage(IMAGE_URL_TEAMS)
                                    .addField("----- Lag Röd -----", teamRed, true)
                                    .addField("----- Lag Blå -----", teamBlue, true)
                                    .setTitle("Lag")))
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage("Ooops nu gick något fel, kanske angav du för få deltagare? Testa igen vettja!")))

                .then();
    }


    public static List<String> setUpTeams(String players) {

        String playersFormatted = players.replaceAll("(?i)!lag ", "");
        playersFormatted = playersFormatted.replace(" ", "");
        List<String> playersList = Arrays.asList(playersFormatted.split(",", -1));

        int numberOfPlayers = playersList.size();

        Collections.shuffle(playersList);

        List<String> teamA = new ArrayList<>(playersList.subList(0, (numberOfPlayers + 1)/2));
        List<String> teamB = new ArrayList<>(playersList.subList((numberOfPlayers + 1)/2, numberOfPlayers));

        var teamAString = teamA.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("\n", "", ""));

        var teamBString = teamB.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("\n", "", ""));


        return Arrays.asList(teamAString, teamBString);
    }
}
