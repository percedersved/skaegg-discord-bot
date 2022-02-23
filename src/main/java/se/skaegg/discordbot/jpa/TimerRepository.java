package se.skaegg.discordbot.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimerRepository extends JpaRepository<TimerEntity, Integer> {
    @Query("SELECT t FROM TimerEntity t WHERE t.key=?1")
    List<TimerEntity> findByTimerKey(String timerKey);
}
