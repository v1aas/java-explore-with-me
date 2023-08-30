package ru.practicum.explorewithme.event.service;

import ru.practicum.explorewithme.event.model.EventDto;

import java.util.List;

public interface EventService {

    public List<EventDto> getEvents(Integer from, Integer size);

    public EventDto getEvent(Integer id);
}
