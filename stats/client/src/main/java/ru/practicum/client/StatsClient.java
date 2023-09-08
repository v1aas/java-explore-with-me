package ru.practicum.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatisticDto;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StatsClient {

    protected final RestTemplate rest;

    public StatsClient(RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://stats:9090"))
                .build();
    }

    public void postStatistic(String ip, String uri, LocalDateTime timeRequest) {
        StatisticDto statisticDto = new StatisticDto();
        statisticDto.setIp(ip);
        statisticDto.setUri(uri);
        statisticDto.setApp("ewm-main-service");
        statisticDto.setTimestamp(LocalDateTime.now());
        try {
            rest.postForObject("/hit", statisticDto, StatisticDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении статистики!" + "\n" + e.getMessage());
        }
    }

    public void getStatistic(String start, String end, List<String> uris, Boolean unique) {
        try {
            rest.getForObject("/stats?start=" + start + "&end=" + end + "&uris=" + uris + "&unique=" + unique,
                    List.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении статистики!");
        }
    }
}