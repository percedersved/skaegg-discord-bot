package se.skaegg.discordbot.handlers;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

public class TeamsVoice implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamsVoice.class);

    @Override
    public Mono<Void> process(Message eventMessage) {

        var players = new StringBuilder();

        eventMessage.getGuild()
                .flatMapMany(Guild::getMembers)
                .subscribe(member -> {
                    var voiceState = member.getVoiceState().blockOptional().orElse(null);
                    LOGGER.debug("se.skaegg.discordbot.handlers.TeamVoice -> Found user {}", member.getDisplayName());
                    LOGGER.debug("se.skaegg.discordbot.handlers.TeamVoice -> The voiceState for this member is: {}", voiceState);
                    if (voiceState != null) {
                        players.append(member.getDisplayName());
                        players.append(",");
                    }
                });



            List<String> teamsList = Teams.setUpTeams(players.toString());
            String teamRed = teamsList.get(0);
            String teamBlue= teamsList.get(1);


        final var IMAGE_URL_TEAMS = "https://static-cdn.jtvnw.net/emoticons/v1/166266/3.0";



        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                    if (teamRed.isBlank() || teamBlue.isBlank())
                    {
                        return channel.createMessage("Tyvärr, Det är ingen som är i en röstkanal och vill leka :sadmudd::koerdittjaeklaboegrace:");
                    }
                    else {
                        return channel.createEmbed(spec ->
                                spec.setColor(Color.of(90, 130, 180))
                                    .setImage(IMAGE_URL_TEAMS)
                                    .addField("Lag Röd", teamRed, true)
                                    .addField("Lag Blå", teamBlue, true)
                                    .setTitle("Lag"));
                    }
                })
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage("Ooops nu gick något fel, Testa igen vettja!")))
                .then();

    }
}
