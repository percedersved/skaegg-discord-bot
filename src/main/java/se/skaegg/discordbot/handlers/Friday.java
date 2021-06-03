package se.skaegg.discordbot.handlers;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Friday implements EventHandler{

    @Override
    public Mono<Void> process(Message eventMessage) {

        LocalDateTime startDay = LocalDate.now().atStartOfDay();
        while (startDay.getDayOfWeek() != DayOfWeek.FRIDAY) {
            startDay = startDay.plusDays(1);
        }

        Duration duration = Duration.between(LocalDateTime.now(), startDay);
        long diff = duration.toMinutes();

        String timeLeft;
        if (diff >= 1440) {
            timeLeft = diff/24/60 + " dagar, " + diff/60%24 + "h, " + diff%60 + "m";
        }
        else if (diff >= 60) {
            timeLeft = diff/60 + "h, " + diff%60 + "m";
        }
        else {
            timeLeft = diff + "m";
        }

        final String finalTimeLeft = timeLeft + " kvar till fredag <:zlatanrage:781224556429312001>";


        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                    if (diff >= 0) {
                        return channel.createMessage(finalTimeLeft);
                    }
                    else {
                        return channel.createEmbed(spec ->
                                spec.setColor(Color.of(90, 130, 180))
                                    .setTitle("Det är FREDAG!!")
                                    .setDescription("(╯°□°）╯︵ ┻━┻"));
                    }
                })
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage("Tyvärr, nåt gick fel.")))
                .then();

    }
}
