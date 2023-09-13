package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.GeneralStatsDto;
import ru.practicum.dto.StatisticDto;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.mapper.StatsMapper;
import ru.practicum.server.model.Statistic;
import ru.practicum.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceDB implements StatsService {

    private final StatsRepository repository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public StatisticDto saveRequest(StatisticDto statisticDto) {
        return StatsMapper.toStatisticDto(repository.save(new Statistic(
                statisticDto.getId(),
                statisticDto.getApp(),
                statisticDto.getUri(),
                statisticDto.getIp(),
                statisticDto.getTimestamp())));
    }

    @Override
    public List<GeneralStatsDto> getStatistic(String startFormat, String endFormat, List<String> uris,
                                              Boolean unique) {
        LocalDateTime start;
        LocalDateTime end;
        try {
            start = LocalDateTime.parse(startFormat, formatter);
            end = LocalDateTime.parse(endFormat, formatter);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Неправильный формат времени!!");
        }

        if (start.isAfter(end) || end.isBefore(start)) {
            throw new ValidationException("Неправильно указано время!");
        }
        if (uris.isEmpty()) {
            if (unique) {
                return repository.getUniqueGeneralStatsDto(start, end);
            } else {
                return repository.getNotUniqueGeneralStatsDto(start, end);
            }
        } else {
            if (unique) {
                return repository.getUniqueGeneralStatsDto(start, end, uris);
            } else {
                return repository.getNotUniqueGeneralStatsDto(start, end, uris);
            }
        }
    }
}