package ru.practicum.statistics.service;

import dto.HitDto;
import dto.StatsDto;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface StatService {

    @Transactional
    void saveHit(HitDto hitDto);

    List<StatsDto> getStats(LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> uris, Boolean unique);
}
