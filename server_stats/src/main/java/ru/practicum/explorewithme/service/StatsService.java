package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.GeneralStatsDto;
import ru.practicum.explorewithme.model.StatisticDto;

import java.util.List;

public interface StatsService {

    StatisticDto saveRequest(StatisticDto statisticDto);

    List<GeneralStatsDto> getStatistic(String start, String end, List<String> uris, Boolean unique);
}