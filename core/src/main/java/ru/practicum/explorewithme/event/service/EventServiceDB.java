package ru.practicum.explorewithme.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.repository.EventRepository;
import ru.practicum.explorewithme.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceDB implements EventService {

    private final EventRepository repository;

    @Override
    public List<Event> getEvents(Integer from, Integer size) {
        return repository.findAll(PageRequest.of(from, size)).toList();
    }

    @Override
    public Event getEvent(Integer id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Такого события нет!");
        }
        return repository.findById(id).get();
    }
}
