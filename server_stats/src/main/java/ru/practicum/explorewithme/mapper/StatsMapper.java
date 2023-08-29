package ru.practicum.explorewithme.mapper;

import ru.practicum.explorewithme.model.Statistic;
import ru.practicum.explorewithme.model.StatisticDto;

import java.util.ArrayList;
import java.util.List;

public class StatsMapper {

    public static StatisticDto toStatisticDto(Statistic stats) {
        return new StatisticDto(stats.getId(), stats.getApp(), stats.getUri(), stats.getIp(),
                stats.getTimestamp().toString());
    }

    public static List<StatisticDto> toListStatisticDto(List<Statistic> statisticList) {
        List<StatisticDto> statisticsDto = new ArrayList<>();
        for (Statistic statistic : statisticList) {
            statisticsDto.add(toStatisticDto(statistic));
        }
        return statisticsDto;
    }
}