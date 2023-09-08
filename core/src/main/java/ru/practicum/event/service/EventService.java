package ru.practicum.event.service;

import ru.practicum.event.model.EventDto;
import ru.practicum.event.model.EventDtoRequest;
import ru.practicum.event.model.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    public List<EventDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime start,
                                    LocalDateTime end, Boolean onlyAvailable, String sort, Integer from, Integer size,
                                    HttpServletRequest request);

    public EventDto getEvent(Integer id);

    public List<EventDto> getAdminEvents(List<Long> users, List<State> states, List<Long> categories,
                                         String start, String end, Integer from, Integer size);

    public EventDto patchAdminEvent(Integer eventId, EventDtoRequest eventDtoRequest);
}