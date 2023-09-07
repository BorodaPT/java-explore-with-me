package ru.practicum.statistics.mapper;

import dto.HitDto;
import ru.practicum.statistics.model.StatsView;

public class MapperStat {

    public static StatsView toStatsView(HitDto hitDto) {
        return new StatsView(
                hitDto.getId(),
                hitDto.getApp(),
                hitDto.getIp(),
                hitDto.getUri(),
                hitDto.getTimestamp());
    }

}
