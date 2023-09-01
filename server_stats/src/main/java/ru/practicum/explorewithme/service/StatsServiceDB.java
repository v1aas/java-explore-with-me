package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.GeneralStatsDto;
import ru.practicum.explorewithme.mapper.StatsMapper;
import ru.practicum.explorewithme.model.Statistic;
import ru.practicum.explorewithme.model.StatisticDto;
import ru.practicum.explorewithme.repository.StatsRepository;

import javax.validation.ValidationException;
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
                LocalDateTime.parse(statisticDto.getTimestamp(), formatter))));
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