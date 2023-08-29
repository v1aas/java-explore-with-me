package ru.practicum.explorewithme.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.service.EventService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService service;

    @GetMapping
    public List<Event> getEvents(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return service.getEvents(from, size);
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable Integer id) {
        return service.getEvent(id);
    }
}