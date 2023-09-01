package ru.practicum.explorewithme.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventDto;
import ru.practicum.explorewithme.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class PublicUserController {

    private final UserService service;

    @PostMapping("/{userId}/events")
    public Event postEvent(@PathVariable int userId, @RequestBody EventDto eventDto) {
        log.info("Create new event for user: {}", userId);
        return service.createEventForUser(userId, eventDto);
    }

    @GetMapping("/{userId}/events")
    public List<Event> getEvents(@PathVariable int userId,
                                 @RequestParam(value = "from", defaultValue = "0") Integer from,
                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get user {} events", userId);
        return service.getEventsForUser(userId, from, size);
    }
}