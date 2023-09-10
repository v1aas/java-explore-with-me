package ru.practicum.client;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.GeneralStatsDto;
import ru.practicum.dto.StatisticDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StatsClient {

    protected final RestTemplate rest;

    public StatsClient(RestTemplateBuilder builder) {
        this.rest = builder
                                                                                // TODO исправить на stats
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:9090"))
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

    public List<GeneralStatsDto> getStatistic(String start, String end, List<String> uris, Boolean unique) {
        try {
            String urisParam = uris.stream()
                    .map(uri -> "uris=" + uri)
                    .collect(Collectors.joining("&"));
            ResponseEntity<String> response = rest.getForEntity("/stats?start=" + start + "&end=" + end + "&"
                    + urisParam + "&unique=" + unique, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(response.getBody(), new TypeReference<List<GeneralStatsDto>>() {
                });
            } else {
                throw new RuntimeException("Ошибка при получении статистики: " + response.getStatusCode() + "\n " +
                        response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении статистики!");
        }
    }
}