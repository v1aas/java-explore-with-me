package ru.practicum.event.mapper;

import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventDto;
import ru.practicum.event.model.EventDtoRequest;
import ru.practicum.event.model.Location;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventMapper {

    public static Event toEvent(EventDtoRequest eventDto, User user) {
        Event event = new Event();
        event.setId(eventDto.getId());
        event.setAnnotation(eventDto.getAnnotation());
        event.setCategory(new Category(eventDto.getCategory(), null));
        event.setDescription(eventDto.getDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setLon(eventDto.getLocation().getLon());
        event.setLat(eventDto.getLocation().getLat());
        event.setPaid(eventDto.getPaid());
        event.setParticipantLimit(eventDto.getParticipantLimit());
        event.setTitle(eventDto.getTitle());
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        return event;
    }

    public static EventDto toEventDto(Event event) {
        return new EventDto(event.getId(),
                event.getAnnotation(),
                event.getCategory(),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getInitiator(),
                new Location(event.getLat(), event.getLon()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.isRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews());
    }

    public static List<EventDto> toEventDtoList(List<Event> events) {
        List<EventDto> eventDto = new ArrayList<>();
        for (Event event : events) {
            eventDto.add(toEventDto(event));
        }
        return eventDto;
    }
}