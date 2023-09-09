package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.model.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.model.CompilationDtoRequest;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.event.model.EventDto;
import ru.practicum.event.model.EventDtoRequest;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;
import ru.practicum.user.model.UserDto;
import ru.practicum.user.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {
    private final CategoryService categoryService;
    private final UserService userService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(value = "ids", defaultValue = "0") List<Integer> ids,
                                  @RequestParam(value = "from", defaultValue = "0") Integer from,
                                  @RequestParam(value = "size", defaultValue = "10")
                                  Integer size) {
        log.info("Get users");
        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@RequestBody UserDto userDto) {
        log.info("Post user: {}", userDto);
        return userService.createUser(userDto);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public UserDto deleteUser(@PathVariable Integer userId) {
        log.info("Delete user: {}", userId);
        return userService.deleteUser(userId);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Post category: {}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping("/categories/{id}")
    public CategoryDto patchCategory(@PathVariable Integer id, @RequestBody CategoryDto categoryDto) {
        log.info("Patch category: {}", categoryDto);
        return categoryService.updateCategory(id, categoryDto);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CategoryDto deleteCategory(@PathVariable Integer id) {
        log.info("Delete category: {}", id);
        return categoryService.deleteCategory(id);
    }

    @PatchMapping("/events/{eventId}")
    public EventDto patchEvent(@PathVariable Integer eventId, @RequestBody EventDtoRequest eventDtoRequest) {
        return eventService.patchAdminEvent(eventId, eventDtoRequest);
    }

    @GetMapping("/events")
    public List<EventDto> getEvents(@RequestParam(value = "users", defaultValue = "") List<Long> users,
                                    @RequestParam(value = "state", defaultValue = "") List<State> state,
                                    @RequestParam(value = "categories", defaultValue = "") List<Long> categories,
                                    @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                    @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return eventService.getAdminEvents(users, state, categories, rangeStart, rangeEnd, from, size);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto postCompilations(@RequestBody CompilationDtoRequest compilationDtoRequest) {
        return compilationService.createCompilation(compilationDtoRequest);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto patchCompilation(@PathVariable Integer compId,
                                           @RequestBody CompilationDtoRequest compilationDtoRequest) {
        return compilationService.updateCompilation(compId, compilationDtoRequest);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable Integer compId) {
        compilationService.deleteCompilation(compId);
    }
}