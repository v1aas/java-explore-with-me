package ru.practicum.explorewithme.event.service;

import ru.practicum.explorewithme.event.model.Event;

import java.util.List;

public interface EventService {

    public List<Event> getEvents(Integer from, Integer size);

    public Event getEvent(Integer id);
}
