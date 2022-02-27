package se.skaegg.discordbot.handlers;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import se.skaegg.discordbot.jpa.TimerEntity;
import se.skaegg.discordbot.jpa.TimerRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Timer implements EventHandler{

    TimerRepository timerRepository;
    private static final String ERROR_MESSAGE = "Tyvärr nåt gick fel";

    public Timer(TimerRepository timerRepository) {
        this.timerRepository = timerRepository;
    }


    @Override
    public Mono<Void> process(Message eventMessage) {

        String timerFormatted = eventMessage.getContent().replace("!nynedräkning ", "");

        String timerKey = null;
        String timerDateTimeString = null;
        boolean timerAlreadyExists = false;
        boolean noKeyAdded = false;
        Pattern pattern = Pattern.compile("(.*),\\s*(.*)");
        Matcher matcher = pattern.matcher(timerFormatted);
        if (matcher.find()) {
            timerKey = matcher.group(1);
            timerDateTimeString = matcher.group(2);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        assert timerDateTimeString != null;
        LocalDateTime timerDateTime = LocalDateTime.parse(timerDateTimeString, formatter);

        TimerEntity timer = new TimerEntity();
        timer.setKey(timerKey);
        timer.setTimeDateTime(timerDateTime);
        timer.setProcessed(false);
        Snowflake originChannel = Objects.requireNonNull(eventMessage.getChannel().doOnSuccess(MessageChannel::getId).block()).getId();
        timer.setChannelId(originChannel.asString());

        if (!timerRepository.findByTimerKey(timerFormatted).isEmpty()) {
            timerAlreadyExists = true;
        }
        else if (timerKey.isBlank() || timerDateTimeString.isBlank()) {
            noKeyAdded = true;
        }
        else {
            timerRepository.save(timer);
        }

        String finalTimerKey = timerKey;
        String finalTimerDateTimeString = timerDateTimeString;
        boolean finalTimerAlreadyExists = timerAlreadyExists;
        boolean finalNoKeyAdded = noKeyAdded;
        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                    if (finalTimerAlreadyExists) {
                        return channel.createMessage("Det finns redan en nedräkning med det namnet, testa ett annat namn");
                    }
                    else if (finalNoKeyAdded) {
                        return channel.createMessage("Du måste ange både ett namn på nedräkningen följt av komma och ett datum i formatet yyyy-MM-dd HH:mm");
                    }
                    else {
                        return channel.createMessage("Nedräkning med namn " + finalTimerKey + " och datum " + finalTimerDateTimeString + " har lagts till");
                    }
                })
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage(ERROR_MESSAGE)))
                .then();
    }


    public Mono<Void> checkTimer(Message eventMessage) {
        String askedTimer = eventMessage.getContent().replace("!nedräkning ", "");
        List<TimerEntity> timers = timerRepository.findByTimerKey(askedTimer);
        String timeLeft = null;
        TimerEntity timer = null;
        long diff = 0;


        if (!timers.isEmpty()) {
            timer = timers.get(0);
            LocalDateTime expirationDate = timer.getTimeDateTime();

            Duration duration = Duration.between(LocalDateTime.now(), expirationDate);
            diff = duration.toMinutes();


            if (diff >= 1440) {
                timeLeft = diff / 24 / 60 + " dagar, " + diff / 60 % 24 + "h, " + diff % 60 + "m";
            } else if (diff >= 60) {
                timeLeft = diff / 60 + "h, " + diff % 60 + "m";
            } else {
                timeLeft = diff + "m";
            }
            timeLeft = timeLeft + " kvar till " + timer.getKey();
        }


        long finalDiff = diff;
        TimerEntity finalTimer = timer;
        String finalTimeLeft = timeLeft;
        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                    if (finalDiff == 0 || finalTimer == null) {
                        return channel.createMessage("Det finns ingen nedräkning med det namnet");
                    }
                    else if (finalDiff >= 0) {
                        return channel.createMessage(finalTimeLeft);
                    } else {
                        return channel.createMessage("Nedräkning passerad");
                    }
                })
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage(ERROR_MESSAGE)))
                .then();

    }


    public Mono<Void> listAllTimers(Message eventMessage) {
        List<TimerEntity> timers = timerRepository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("Namn")
                .append("\t")
                .append("ID")
                .append("\n");
        for (TimerEntity timer : timers) {
            sb.append(timer.getKey())
                    .append("\t")
                    .append(timer.getId())
                    .append("\n");
        }
        String availableTimers = sb.toString();

        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createEmbed(spec ->
                spec.setColor(Color.of(90, 130, 180))
                        .setTitle("Nedräkningar")
                        .setDescription(availableTimers)
                        ))
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage(ERROR_MESSAGE)))
                .then();
    }


    public Mono<Void> deleteTimer(Message eventMessage) {
        String idToDeleteString = eventMessage.getContent().replace("!tabortnedräkning ", "");
        Integer idToDelete = Integer.parseInt(idToDeleteString);
        boolean timerNotPresent = false;
        String timerNotPresentString = "Det finns ingen nedräkning med det IDt";

        if (timerRepository.findById(idToDelete).isPresent()) {
            timerRepository.deleteById(idToDelete);
        }
        else {
            timerNotPresent = true;
        }

        boolean finalTimerNotPresent = timerNotPresent;
        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                    if (finalTimerNotPresent) {
                        return channel.createMessage(timerNotPresentString);
                    }
                    else {
                        return channel.createMessage("Nedräkning med ID " + idToDelete + " raderades");
                    }
                })
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage(ERROR_MESSAGE)))
                .then();
    }
}