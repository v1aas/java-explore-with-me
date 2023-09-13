package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.GeneralStatsDto;
import ru.practicum.server.model.Statistic;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Statistic, Integer> {
    @Query("SELECT new ru.practicum.dto.GeneralStatsDto(SD.app, SD.uri, COUNT(DISTINCT SD.ip)) " +
            "FROM Statistic SD " +
            "WHERE SD.timestamp BETWEEN :start AND :end " +
            "GROUP BY SD.app, SD.uri " +
            "ORDER BY COUNT(DISTINCT SD.ip) DESC")
    List<GeneralStatsDto> getUniqueGeneralStatsDto(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.GeneralStatsDto(SD.app, SD.uri, COUNT(SD.ip)) " +
            "FROM Statistic SD " +
            "WHERE SD.timestamp BETWEEN :start AND :end " +
            "GROUP BY SD.app, SD.uri " +
            "ORDER BY COUNT(SD.ip) DESC")
    List<GeneralStatsDto> getNotUniqueGeneralStatsDto(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.GeneralStatsDto(SD.app, SD.uri, COUNT(DISTINCT SD.ip)) " +
            "FROM Statistic SD " +
            "WHERE SD.timestamp BETWEEN :start AND :end " +
            "AND SD.uri IN (:uris) " +
            "GROUP BY SD.app, SD.uri " +
            "ORDER BY COUNT(DISTINCT SD.ip) DESC")
    List<GeneralStatsDto> getUniqueGeneralStatsDto(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.dto.GeneralStatsDto(SD.app, SD.uri, COUNT(SD.ip)) " +
            "FROM Statistic SD " +
            "WHERE SD.timestamp BETWEEN :start AND :end " +
            "AND SD.uri IN (:uris) " +
            "GROUP BY SD.app, SD.uri " +
            "ORDER BY COUNT(SD.ip) DESC")
    List<GeneralStatsDto> getNotUniqueGeneralStatsDto(LocalDateTime start, LocalDateTime end, List<String> uris);
}