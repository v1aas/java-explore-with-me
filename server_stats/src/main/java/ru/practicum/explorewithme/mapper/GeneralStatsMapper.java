package ru.practicum.explorewithme.mapper;

import ru.practicum.explorewithme.dto.GeneralStatsDto;
import ru.practicum.explorewithme.model.Statistic;

public class GeneralStatsMapper {

    public static GeneralStatsDto toGeneralStatisticDto(Statistic stats) {
        GeneralStatsDto generalStatsDto = new GeneralStatsDto();
        generalStatsDto.setApp(stats.getApp());
        generalStatsDto.setUri(stats.getUri());
        return generalStatsDto;
    }
}