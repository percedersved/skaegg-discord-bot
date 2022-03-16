package se.skaegg.discordbot.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimerRepository extends JpaRepository<TimerEntity, Integer> {
    List<TimerEntity> findByKeyIgnoreCase(String timerKey);

    List<TimerEntity> findByProcessed(Boolean processed);
}
