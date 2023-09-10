package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.model.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.event.model.EventDto;
import ru.practicum.event.model.EventDtoRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.model.AllUserRequestFormat;
import ru.practicum.request.model.AllUserRequestResponse;
import ru.practicum.request.model.UserRequestDto;
import ru.practicum.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
public class PublicController {
    private final CategoryService categoryService;
    private final EventService eventService;
    private final UserService userService;
    private final CompilationService compilationService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto postEvent(@PathVariable int userId, @RequestBody EventDtoRequest eventDto) {
        log.info("Create new event for user: {}", userId);
        return userService.createEventForUser(userId, eventDto);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventDto> getEvents(@PathVariable int userId,
                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get user {} events", userId);
        return userService.getEventsForUser(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventDto getEventForUser(@PathVariable int userId, @PathVariable int eventId) {
        log.info("Get event {} for user {}", eventId, userId);
        return userService.getEventForUser(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventDto patchEventForOwner(@PathVariable int userId, @PathVariable int eventId,
                                       @RequestBody EventDtoRequest eventDtoRequest) {
        log.info("Update event {} for user {}", eventId, userId);
        return userService.updateEventForOwner(userId, eventId, eventDtoRequest);
    }

    @GetMapping("/users/{userId}/requests")
    public List<UserRequestDto> getAllRequestForUser(@PathVariable int userId) {
        log.info("Get all requests for user {}", userId);
        return userService.getRequestsForUser(userId);
    }

    @PostMapping("/users/{userId}/requests")
    public UserRequestDto postRequestForUser(@PathVariable int userId, @RequestParam int eventId) {
        log.info("Post user request for event {}", eventId);
        return userService.createRequestForEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public UserRequestDto patchCancelRequest(@PathVariable int userId, @PathVariable int requestId) {
        log.info("Cancel request id: {}", requestId);
        return userService.cancelUserRequestForEvent(userId, requestId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<UserRequestDto> getAllRequestForOwnerEvent(@PathVariable int userId, @PathVariable int eventId) {
        log.info("Get all request for owner {} event {}", userId, eventId);
        return userService.getAllRequestsForOwnerEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public AllUserRequestResponse patchRequestForOwnerEvent(@PathVariable int userId, @PathVariable int eventId,
                                                            @RequestBody AllUserRequestFormat format) {
        log.info("Change status UserRequest: {}", format);
        return userService.changeStatusRequestForEvent(userId, eventId, format);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get all categories");
        return categoryService.getAllCategory(from, size);
    }

    @GetMapping("/categories/{id}")
    public CategoryDto getCategory(@PathVariable Integer id) {
        log.info("Get category: {}", id);
        return categoryService.getCategory(id);
    }

    @GetMapping("/events")
    public List<EventDto> getEvents(@RequestParam(value = "text", defaultValue = "") String text,
                                    @RequestParam(value = "categories", defaultValue = "") List<Long> categories,
                                    @RequestParam(value = "paid", required = false) Boolean paid,
                                    @RequestParam(value = "rangeStart", required = false) LocalDateTime rangeStart,
                                    @RequestParam(value = "rangeEnd", required = false) LocalDateTime rangeEnd,
                                    @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                    @RequestParam(value = "sort", defaultValue = "EVENT_DATE") String sort,
                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @RequestParam(value = "size", defaultValue = "10") Integer size,
                                    HttpServletRequest request) {
        log.info("Get events for ip: {}", request.getRemoteAddr());
        return eventService.getEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/events/{id}")
    public EventDto getEvent(@PathVariable Integer id, HttpServletRequest request) {
        log.info("Get event: {}", id);
        return eventService.getEvent(id, request);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getAllCompilations(@RequestParam(value = "pinned", defaultValue = "false") Boolean pinned,
                                                   @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                   @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get all compilations");
        return compilationService.getAllCompilation(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable Integer compId) {
        log.info("Get compilation: {}", compId);
        return compilationService.getCompilation(compId);
    }
}