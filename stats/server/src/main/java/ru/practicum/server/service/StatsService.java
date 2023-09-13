package ru.practicum.server.service;

import ru.practicum.dto.GeneralStatsDto;
import ru.practicum.dto.StatisticDto;

import java.util.List;

public interface StatsService {

    StatisticDto saveRequest(StatisticDto statisticDto);

    List<GeneralStatsDto> getStatistic(String start, String end, List<String> uris, Boolean unique);
}