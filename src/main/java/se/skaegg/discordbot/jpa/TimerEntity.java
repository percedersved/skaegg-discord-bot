package se.skaegg.discordbot.jpa;

import javax.persistence.*;
import java.time.LocalDateTime;



@Entity
@Table(name = "timers")
public class TimerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "timer_key", unique = true, nullable = false)
    String key;

    @Column(name = "timer_date_time")
    LocalDateTime timeDateTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LocalDateTime getTimeDateTime() {
        return timeDateTime;
    }

    public void setTimeDateTime(LocalDateTime timeDateTime) {
        this.timeDateTime = timeDateTime;
    }
}
