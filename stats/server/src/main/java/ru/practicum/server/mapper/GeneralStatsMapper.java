package ru.practicum.server.mapper;

import ru.practicum.dto.GeneralStatsDto;
import ru.practicum.server.model.Statistic;

public class GeneralStatsMapper {

    public static GeneralStatsDto toGeneralStatisticDto(Statistic stats) {
        GeneralStatsDto generalStatsDto = new GeneralStatsDto();
        generalStatsDto.setApp(stats.getApp());
        generalStatsDto.setUri(stats.getUri());
        return generalStatsDto;
    }
}