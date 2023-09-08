package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventDto;
import ru.practicum.event.model.EventDtoRequest;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ResourceNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.mapper.UserRequestMapper;
import ru.practicum.request.model.*;
import ru.practicum.request.repository.UserRequestRepository;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserDto;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceDB implements UserService {
    private final UserRepository repository;

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final UserRequestRepository requestRepository;

    @Override
    public List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size) {
        List<UserDto> users;
        if (ids.size() == 1 && ids.get(0) == 0) {
            users = UserMapper.toUserDtoList(repository.findAll(PageRequest.of(from, size)).toList());
        } else {
            users = UserMapper.toUserDtoList(repository.findByIdIn(ids, PageRequest.of(from, size)).toList());
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
        UserDto user = UserMapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет!")));
        repository.deleteById(id);
        return user;
    }

    @Override
    public EventDto createEventForUser(Integer userId, EventDtoRequest event) {
        if (repository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Такого пользователя не существует");
        }
        Event newEvent = EventMapper.toEvent(event, repository.findById(userId).get());
        newEvent.setCategory(categoryRepository.findById(newEvent.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Такой категории не существует!")));
        newEvent.setState(State.WAIT);
        return EventMapper.toEventDto(eventRepository.save(newEvent));
    }

    @Override
    public List<EventDto> getEventsForUser(Integer userId, Integer from, Integer size) {
        if (repository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Такого пользователя не существует");
        }
        return EventMapper.toEventDtoList(eventRepository.findByInitiator(repository.findById(userId).get(),
                PageRequest.of(from / size, size)).toList());
    }

    @Override
    public EventDto getEventForUser(Integer userId, Integer eventId) {
        if (repository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Такого пользователя не существует");
        }
        if (eventRepository.findByInitiatorAndId(repository.findById(userId).get(), eventId).isEmpty()) {
            throw new ResourceNotFoundException("Данный пользователь не создатель события");
        }
        return EventMapper.toEventDto(eventRepository.findByInitiatorAndId(repository
                .findById(userId).get(), eventId).get());
    }

    @Override
    public EventDto updateEventForOwner(Integer userId, Integer eventId, EventDtoRequest event) {
        if (repository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Такого пользователя не существует");
        }
        if (eventRepository.findByInitiatorAndId(repository.findById(userId).get(), eventId).isEmpty()) {
            throw new ValidationException("Данный пользователь не создатель события");
        }
        Event newEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Такого события нет"));
        newEvent.setAnnotation(event.getAnnotation());
        newEvent.setCategory(categoryRepository.findById(event.getCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Такой категории не существует")));
        newEvent.setDescription(event.getDescription());
        newEvent.setEventDate(event.getEventDate());
        newEvent.setLat(event.getLocation().getLat());
        newEvent.setLon(event.getLocation().getLon());
        newEvent.setPaid(event.isPaid());
        newEvent.setParticipantLimit(event.getParticipantLimit());
        newEvent.setRequestModeration(event.isRequestModeration());
        newEvent.setTitle(event.getTitle());
        return EventMapper.toEventDto(eventRepository.save(newEvent));
    }

    @Override
    public List<UserRequestDto> getAllRequestsForOwnerEvent(Integer userId, Integer eventId) {
        if (!eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Такого события нет!"))
                .getInitiator().getId().equals(userId)) {
            throw new ValidationException("Посмотреть все запросы может только его создатель!");
        }
        return UserRequestMapper.toRequestDtoList(
                requestRepository.findAllByEvent(
                        eventRepository.findById(eventId)
                                        .orElseThrow(() -> new ResourceNotFoundException("Такого события нет!"))));
    }

    @Override
    public AllUserRequestResponse changeStatusRequestForEvent(Integer userId, Integer eventId,
                                                              AllUserRequestFormat request) {
        if (!eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Такого события нет!"))
                .getInitiator().getId().equals(userId)) {
            throw new ValidationException("Изменять запросы может только его создатель!");
        }
        for (Integer id : request.getRequestIds()) {
            UserRequest userRequest = requestRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Запроса с id " + id + "нет!"));
            userRequest.setStatus(request.getStatus());
            requestRepository.save(userRequest);
        }
        return new AllUserRequestResponse(
                UserRequestMapper.toRequestDtoList(requestRepository.findAllByStatus(Status.CONFIRMED)),
                UserRequestMapper.toRequestDtoList(requestRepository.findAllByStatus(Status.CANCELLED)));
    }

    @Override
    public UserRequestDto createRequestForEvent(Integer userId, Integer eventId) {
        for (UserRequest request : requestRepository.findAll()) {
            if (request.getEvent().getId().equals(eventId) && request.getRequester().getId().equals(userId)) {
                throw new ConflictException("Нельзя делать повторный запрос!");
            }
            if (request.getEvent().getInitiator().getId().equals(userId)) {
                throw new ConflictException("Инициатор не может делать запрос на свое событие!");
            }
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Такого события нет!"));
        if (event.getCreatedOn() == null) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии!");
        }
        if (event.getConfirmedRequests() != null) {
            if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                throw new ConflictException("Достигнут лимит запросов на участие!");
            }
        }
        UserRequest userRequest = new UserRequest();
        userRequest.setCreated(LocalDateTime.now());
        userRequest.setEvent(event);
        userRequest.setRequester(repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя не существует!")));
        userRequest.setStatus(Status.PENDING);
        return UserRequestMapper.toRequestDto(requestRepository.save(userRequest));
    }

    @Override
    public List<UserRequestDto> getRequestsForUser(Integer userId) {
        return UserRequestMapper.toRequestDtoList(
                requestRepository.findAllByRequester(
                        repository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя не существует!"))));
    }

    @Override
    public UserRequestDto cancelUserRequestForEvent(Integer userId, Integer requestId) {
        if (!requestRepository.findById(requestId).orElseThrow(() -> new ResourceNotFoundException("Такого запроса нет!"))
                .getRequester().getId().equals(userId)) {
            throw new ValidationException("Отменить запрос может только его создатель!");
        }
        UserRequest userRequest = requestRepository
                .findById(requestId).orElseThrow(() -> new ResourceNotFoundException("Такого события нет!"));
        userRequest.setStatus(Status.CANCELLED);
        return UserRequestMapper.toRequestDto(userRequest);
    }


}