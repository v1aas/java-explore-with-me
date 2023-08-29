package ru.practicum.explorewithme.event.mapper;

import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventDto;
import ru.practicum.explorewithme.user.model.User;

public class EventMapper {

    public static Event toEvent(EventDto eventDto, User user) {
        return new Event(eventDto.getId(), eventDto.getAnnotation(), eventDto.getDescription(), user);
    }
}
