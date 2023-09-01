package ru.practicum.explorewithme.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.event.mapper.EventMapper;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.EventDto;
import ru.practicum.explorewithme.event.repository.EventRepository;
import ru.practicum.explorewithme.exception.ResourceNotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.user.mapper.UserMapper;
import ru.practicum.explorewithme.user.model.User;
import ru.practicum.explorewithme.user.model.UserDto;
import ru.practicum.explorewithme.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceDB implements UserService {
    private final UserRepository repository;

    private final EventRepository eventRepository;

    @Override
    public List<UserDto> getUsers(Integer id, Integer from, Integer size) {
        List<UserDto> users = new ArrayList<>();
        if (id == 0 && from != null && size != null) {
            users = UserMapper.toUserDtoList(repository.findAll(PageRequest.of(from, size)).toList());
        } else {
            users.add(UserMapper.toUserDto(repository.findById(id).get()));
        }
        return users;
    }

    @Override
    public UserDto createUser(UserDto user) {
        if (user.getName() == null || user.getEmail() == null) {
            throw new ValidationException("Нужно заполнить все поля!");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Почта некорректна");
        }
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(user)));
    }

    @Override
    public UserDto updateUser(Integer id, UserDto user) {
        user.setId(id);
        User existsUser = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Такого пользователя не существует"));
        if (user.getName() != null) {
            existsUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existsUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(repository.save(existsUser));
    }

    @Override
    public UserDto deleteUser(Integer id) {
        UserDto user = UserMapper.toUserDto(repository.findById(id).get());
        repository.deleteById(id);
        return user;
    }

    @Override
    public Event createEventForUser(Integer userId, EventDto event) {
        if (repository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Такого пользователя не существует");
        }
        return eventRepository.save(EventMapper.toEvent(event, repository.findById(userId).get()));
    }

    @Override
    public List<Event> getEventsForUser(Integer userId, Integer from, Integer size) {
        if (repository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Такого пользователя не существует");
        }
        return eventRepository.findByInitiator(repository.findById(userId).get(),
                PageRequest.of(from / size, size)).toList();
    }
}