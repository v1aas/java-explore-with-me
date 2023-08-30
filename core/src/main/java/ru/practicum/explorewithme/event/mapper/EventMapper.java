package ru.practicum.explorewithme.event.mapper;

import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventDto;
import ru.practicum.explorewithme.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class EventMapper {

    public static Event toEvent(EventDto eventDto, User user) {
        return new Event(eventDto.getId(), eventDto.getAnnotation(), eventDto.getDescription(), user);
    }

    public static EventDto toEventDto(Event event) {
        return new EventDto(event.getId(), event.getAnnotation(), event.getDescription(), event.getInitiator());
    }

    public static List<EventDto> toEventDtoList(List<Event> events) {
        List<EventDto> eventDto = new ArrayList<>();
        for (Event event : events) {
            eventDto.add(toEventDto(event));
        }
        return eventDto;
    }
}