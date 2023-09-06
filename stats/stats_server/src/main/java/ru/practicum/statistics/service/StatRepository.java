package ru.practicum.statistics.service;

import dto.StatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.statistics.model.StatsView;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository  extends JpaRepository<StatsView, Long> {

    @Query(value = "select new dto.StatsDto(" +
            "sv.nameApp," +
            "sv.url," +
            "count(sv.fromIp)" +
            ") " +
            "from StatsView as sv " +
            "where sv.viewed BETWEEN :startDateTime AND :endDateTime AND sv.url IN (:uris) " +
            "group by sv.nameApp, sv.url " +
            "order by sv.url")
    List<StatsDto> getStats(LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> uris);

    @Query(value = "select new dto.StatsDto(" +
            "sv.nameApp," +
            "sv.url," +
            "count(distinct(sv.fromIp))" +
            ") " +
            "from StatsView as sv " +
            "where sv.viewed BETWEEN :startDateTime AND :endDateTime AND sv.url IN (:uris) " +
            "group by sv.nameApp, sv.url " +
            "order by sv.url")
    List<StatsDto> getStatsUnique(LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> uris);
}
