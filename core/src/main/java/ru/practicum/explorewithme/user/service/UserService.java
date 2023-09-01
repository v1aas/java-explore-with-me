package ru.practicum.explorewithme.user.service;

import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventDto;
import ru.practicum.explorewithme.user.model.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(Integer id, Integer from, Integer size);

    UserDto createUser(UserDto user);

    UserDto updateUser(Integer id, UserDto user);

    UserDto deleteUser(Integer id);

    Event createEventForUser(Integer userId, EventDto event);

    List<Event> getEventsForUser(Integer userId, Integer from, Integer size);
}