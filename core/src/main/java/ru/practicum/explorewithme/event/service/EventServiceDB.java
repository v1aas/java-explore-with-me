package ru.practicum.explorewithme.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.event.mapper.EventMapper;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventDto;
import ru.practicum.explorewithme.event.repository.EventRepository;
import ru.practicum.explorewithme.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceDB implements EventService {

    private final EventRepository repository;

    @Override
    public List<EventDto> getEvents(Integer from, Integer size) {
        return EventMapper.toEventDtoList(repository.findAll(PageRequest.of(from, size)).toList());
    }

    @Override
    public EventDto getEvent(Integer id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Такого события нет!");
        }
        return EventMapper.toEventDto(repository.findById(id).get());
    }
}
