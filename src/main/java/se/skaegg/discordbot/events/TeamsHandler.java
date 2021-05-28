package se.skaegg.discordbot.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TeamsHandler {

    public static String setUpTeams(String players) {

        String playersFormatted = players.replace("!lag ", "");
        playersFormatted = playersFormatted.replace(" ", "");
        List<String> playersList = Arrays.asList(playersFormatted.split(",", -1));

        int numberOfPlayers = playersList.size();

        Collections.shuffle(playersList);

        List<String> teamA = new ArrayList<>(playersList.subList(0, (numberOfPlayers + 1)/2));
        List<String> teamB = new ArrayList<>(playersList.subList((numberOfPlayers + 1)/2, numberOfPlayers));

        var teamAString = teamA.stream()
                .map(n -> String.valueOf(n))
                .collect(Collectors.joining(" | ", "", ""));

        var teamBString = teamB.stream()
                .map(n -> String.valueOf(n))
                .collect(Collectors.joining(" | ", "", ""));

        return "```\n" +
            "Blå:\t" + teamAString + "\n" +
            "Röd:\t" + teamBString + "```";
    }
}
