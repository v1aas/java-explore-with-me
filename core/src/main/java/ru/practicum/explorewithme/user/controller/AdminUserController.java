package ru.practicum.explorewithme.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.user.model.UserDto;
import ru.practicum.explorewithme.user.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(value = "id", defaultValue = "0") Integer id,
                                  @RequestParam(value = "from", defaultValue = "0") Integer from,
                                  @RequestParam(value = "size", defaultValue = "10")
                                  Integer size) {
        log.info("Get user/s");
        return service.getUsers(id, from, size);
    }

    @PostMapping
    public UserDto postUser(@RequestBody UserDto userDto) {
        log.info("Post user: {}", userDto);
        return service.createUser(userDto);
    }
}
