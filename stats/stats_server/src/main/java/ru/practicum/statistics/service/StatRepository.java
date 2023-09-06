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
            "WHERE sv.viewed BETWEEN :startDateTime AND :endDateTime " +
            "AND (coalesce(:uris, null) is null or (sv.url in :uris)) " +
            "group by sv.nameApp, sv.url " +
            "order by 3 desc")
    List<StatsDto> getStats(LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> uris);

    @Query(value = "select new dto.StatsDto(" +
            "sv.nameApp," +
            "sv.url," +
            "count(distinct(sv.fromIp))" +
            ") " +
            "from StatsView as sv " +
            "where sv.viewed BETWEEN :startDateTime AND :endDateTime " +
            "AND (coalesce(:uris, null) is null or (sv.url in :uris)) " +
            "group by sv.nameApp, sv.url " +
            "order by 3 desc")
    List<StatsDto> getStatsUnique(LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> uris);
}
