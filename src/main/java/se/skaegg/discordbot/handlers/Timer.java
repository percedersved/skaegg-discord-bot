package se.skaegg.discordbot.handlers;

import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import se.skaegg.discordbot.jpa.TimerEntity;
import se.skaegg.discordbot.jpa.TimerRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Timer implements EventHandler{

    TimerRepository timerRepository;

    public Timer(TimerRepository timerRepository) {
        this.timerRepository = timerRepository;
    }


    @Override
    public Mono<Void> process(Message eventMessage) {

        String timerFormatted = eventMessage.getContent().replace("!nynedräkning ", "");

        String timerKey = null;
        String timerDateTimeString = null;
        Pattern pattern = Pattern.compile("(.*),\\s*(.*)");
        Matcher matcher = pattern.matcher(timerFormatted);
        if (matcher.find()) {
            timerKey = matcher.group(1);
            timerDateTimeString = matcher.group(2);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime timerDateTime = LocalDateTime.parse(timerDateTimeString, formatter);

        TimerEntity timer = new TimerEntity();
        timer.setKey(timerKey);
        timer.setTimeDateTime(timerDateTime);

        timerRepository.save(timer);

        String finalTimerKey = timerKey;
        String finalTimerDateTimeString = timerDateTimeString;
        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Timer with key " + finalTimerKey + " and expiration date " + finalTimerDateTimeString + " was added"))
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage("Tyvärr, nåt gick fel.")))
                .then();
    }


    public Mono<Void> checkTimer(Message eventMessage) {
        String askedTimer = eventMessage.getContent().replace("!nedräkning ", "");
        List<TimerEntity> timers = timerRepository.findByTimerKey(askedTimer);

        TimerEntity timer = timers.get(0);
        LocalDateTime expirationDate = timer.getTimeDateTime();


        Duration duration = Duration.between(LocalDateTime.now(), expirationDate);
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

        final String finalTimeLeft = timeLeft + " kvar till " + timer.getKey();

        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> {
                    if (diff >= 0) {
                        return channel.createMessage(finalTimeLeft);
                    } else {
                        return channel.createMessage("Timer passerad");
                    }
                })
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage("Tyvärr, nåt gick fel.")))
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
                .flatMap(channel -> channel.createMessage("Available timers are: \n" + availableTimers))
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage("Tyvärr, nåt gick fel.")))
                .then();
    }


    public Mono<Void> deleteTimer(Message eventMessage) {
        String idToDeleteString = eventMessage.getContent().replace("!tabortnedräkning ", "");
        Integer idToDelete = Integer.parseInt(idToDeleteString);

        timerRepository.deleteById(idToDelete);

        return Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Timer with ID " + idToDelete + " was deleted"))
                .onErrorResume(throwable -> eventMessage.getChannel()
                        .flatMap(messageChannel -> messageChannel.createMessage("Tyvärr, nåt gick fel.")))
                .then();
    }
}
