package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.GeneralStatsDto;
import ru.practicum.dto.StatisticDto;
import ru.practicum.server.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatisticDto postStatistic(@RequestBody StatisticDto statisticDto) {
        log.info("Post new statistic");
        return service.saveRequest(statisticDto);
    }

    @GetMapping("/stats")
    public List<GeneralStatsDto> getStatistic(@RequestParam(value = "start") String start,
                                              @RequestParam(value = "end") String end,
                                              @RequestParam(value = "uris", defaultValue = "") List<String> uris,
                                              @RequestParam(value = "unique", defaultValue = "false") Boolean unique) {
        log.info("Get statistic: start {}; end {}", start, end);
        return service.getStatistic(start, end, uris, unique);
    }
}