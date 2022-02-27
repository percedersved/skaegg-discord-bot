package se.skaegg.discordbot.configuration;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import se.skaegg.discordbot.jpa.TimerEntity;
import se.skaegg.discordbot.jpa.TimerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
public class CheckTimersConfiguration {

    @Value("${text.countdownfinished}")
    String message;

    TimerRepository timerRepository;

    public CheckTimersConfiguration(TimerRepository timerRepository) {
        this.timerRepository = timerRepository;
    }

    @Autowired
    GatewayDiscordClient client;

    @Scheduled(cron = "0 * * * * *")
    public void ScheduleTimerCheck() {
        List<TimerEntity> timers = timerRepository.findAll();

        timers.stream()
                .filter(timer -> timer.getProcessed().equals(Boolean.FALSE))
                .filter(timer -> timer.getTimeDateTime().isBefore(LocalDateTime.now()))
                .forEach(timer -> {
                    client.getChannelById(Snowflake.of(timer.getChannelId()))
                            .ofType(MessageChannel.class)
                            .flatMap(channel -> channel.createMessage(message + " " + timer.getKey()))
                            .subscribe();
                    timer.setProcessed(true);
                    timerRepository.save(timer);
                });
        }
}
