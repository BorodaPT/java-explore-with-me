package ru.practicum.statistics.service;

import dto.HitDto;
import dto.StatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statistics.mapper.MapperStat;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    @Transactional
    @Override
    public void saveHit(HitDto hitDto) {
        statRepository.save(MapperStat.toStatsView(hitDto));
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> uris, Boolean unique) {
        if (unique) {
            return statRepository.getStatsUnique(startDateTime, endDateTime, uris);
        } else {
            return statRepository.getStats(startDateTime, endDateTime, uris);
        }

    }
}
