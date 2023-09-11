package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.client.StatsClient;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventDto;
import ru.practicum.event.model.EventDtoRequest;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ResourceNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceDB implements EventService {

    private final EventRepository repository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatsClient statsClient;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime start,
                                    LocalDateTime end, Boolean onlyAvailable, String sort, Integer from, Integer size,
                                    HttpServletRequest request) {
        List<Category> categoriesQuery = new ArrayList<>();
        List<Boolean> paidQuery = new ArrayList<>();
        LocalDateTime startQuery = Objects.requireNonNullElseGet(start, LocalDateTime::now);
        LocalDateTime endQuery = Objects.requireNonNullElseGet(end, () ->
                LocalDateTime.of(2033, 1, 1, 0, 0, 0));
        String sortQuery;
        if (startQuery.isAfter(endQuery)) {
            throw new ValidationException("Неправильно указано время!");
        }
        if (sort.equalsIgnoreCase("EVENT_DATE")) {
            sortQuery = "eventDate";
        } else if (sort.equalsIgnoreCase("VIEWS")) {
            sortQuery = "views";
        } else {
            throw new ValidationException("Невозможно сортировать по полю " + sort);
        }
        if (categories.isEmpty()) {
            categoriesQuery = null;
        } else {
            for (Long id : categories) {
                categoriesQuery.add(categoryRepository.findById(Math.toIntExact(id))
                        .orElseThrow(() -> new ValidationException("События с " + id + "нет!")));
            }
        }
        if (paid == null) {
            paidQuery = null;
        } else {
            paidQuery.add(paid);
        }
        if (text.isEmpty()) {
            if (onlyAvailable) {
                statsClient.postStatistic(request.getRemoteAddr(), request.getRequestURI(), LocalDateTime.now());
                return EventMapper.toEventDtoList(repository
                        .findByNoTextQueryOnlyAvailable(categoriesQuery, paidQuery, startQuery, endQuery,
                                PageRequest.of(from / size, size, Sort.by(sortQuery))).toList());
            } else {
                statsClient.postStatistic(request.getRemoteAddr(), request.getRequestURI(), LocalDateTime.now());
                return EventMapper.toEventDtoList(repository
                        .findByNoTextQueryNoOnlyAvailable(categoriesQuery, paidQuery, startQuery, endQuery,
                                PageRequest.of(from / size, size, Sort.by(sortQuery))).toList());
            }
        } else {
            if (onlyAvailable) {
                statsClient.postStatistic(request.getRemoteAddr(), request.getRequestURI(), LocalDateTime.now());
                text = "%" + text.toLowerCase() + "%";
                return EventMapper.toEventDtoList(repository
                        .findByAllQueryOnlyAvailable(text, categoriesQuery, paidQuery, startQuery, endQuery,
                                PageRequest.of(from / size, size, Sort.by(sortQuery))).toList());
            } else {
                statsClient.postStatistic(request.getRemoteAddr(), request.getRequestURI(), LocalDateTime.now());
                text = "%" + text.toLowerCase() + "%";
                return EventMapper.toEventDtoList(repository
                        .findByAllQueryNoOnlyAvailable(text, categoriesQuery, paidQuery, startQuery, endQuery,
                                PageRequest.of(from / size, size, Sort.by(sortQuery))).toList());
            }
        }
    }

    @Override
    public EventDto getEvent(Integer id, HttpServletRequest request) {
        Event event = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Такого события нет!"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ResourceNotFoundException("Такого события нет!");
        }
        statsClient.postStatistic(request.getRemoteAddr(), request.getRequestURI(), LocalDateTime.now());
        event.setViews(Math.toIntExact(statsClient.getStatistic(
                "2000-09-01 00:00:00",
                "2032-09-30 00:00:00",
                Collections.singletonList(request.getRequestURI()),
                true).get(0).getHits()));
        return EventMapper.toEventDto(repository.save(event));
    }

    @Override
    public List<EventDto> getAdminEvents(List<Long> users, List<State> states, List<Long> categories,
                                         String start, String end, Integer from, Integer size) {
        List<User> usersQuery = new ArrayList<>();
        List<Category> categoriesQuery = new ArrayList<>();
        List<State> statesQuery;
        LocalDateTime startQuery;
        LocalDateTime endQuery;
        try {
            if (start != null) {
                startQuery = LocalDateTime.parse(start, formatter);
            } else {
                startQuery = LocalDateTime.now();
            }
            if (end != null) {
                endQuery = LocalDateTime.parse(end, formatter);
            } else {
                endQuery = LocalDateTime.now().plusYears(10);
            }
        } catch (RuntimeException e) {
            throw new ValidationException(e.getMessage());
        }
        if (users.isEmpty()) {
            usersQuery = null;
        } else {
            if (users.size() == 1 && users.get(0) == 0) {
                usersQuery = null;
            } else {
                for (Long id : users) {
                    usersQuery.add(userRepository.findById(Math.toIntExact(id))
                            .orElseThrow(() -> new ValidationException("Пользователя с " + id + " не существует!")));
                }
            }
        }
        if (categories.isEmpty()) {
            categoriesQuery = null;
        } else {
            if (categories.size() == 1 && categories.get(0) == 0) {
                categoriesQuery = null;
            } else {
                for (Long id : categories) {
                    categoriesQuery.add(categoryRepository.findById(Math.toIntExact(id))
                            .orElseThrow(() -> new ValidationException("Категории с " + id + " не существует!")));
                }
            }
        }
        if (states.isEmpty()) {
            statesQuery = null;
        } else {
            statesQuery = new ArrayList<>(states);
        }
        return EventMapper.toEventDtoList(repository.findByAdminQuery(usersQuery, statesQuery, categoriesQuery,
                startQuery, endQuery, PageRequest.of(from, size)).toList());
    }

    @Override
    public EventDto patchAdminEvent(Integer eventId, EventDtoRequest eventDtoRequest) {
        Event oldEvent = repository.findById(eventId).orElseThrow(() -> new ValidationException("Такого события нет!"));
        if (eventDtoRequest.getCategory() != null) {
            oldEvent.setCategory(categoryRepository.findById(eventDtoRequest.getCategory())
                    .orElseThrow(() -> new ValidationException("Такой категории не существует!")));
        }
        if (eventDtoRequest.getEventDate() != null) {
            if (eventDtoRequest.getEventDate().isBefore(oldEvent.getEventDate()) ||
                    eventDtoRequest.getEventDate().isBefore(LocalDateTime.now())) {
                throw new ValidationException("Невозможно установить такую дату");
            }
            oldEvent.setEventDate(eventDtoRequest.getEventDate());
        }
        if (eventDtoRequest.getDescription() != null) {
            if (eventDtoRequest.getDescription().trim().isEmpty()
                    || eventDtoRequest.getDescription().length() < 20
                    || eventDtoRequest.getDescription().length() > 7000) {
                throw new ValidationException("Описание не должно быть пустым и быть >20 и <7000 символов!");
            } else {
                oldEvent.setDescription(eventDtoRequest.getDescription());
            }
        }
        if (eventDtoRequest.getAnnotation() != null) {
            if (eventDtoRequest.getAnnotation().trim().isEmpty()
                    || eventDtoRequest.getAnnotation().length() < 20
                    || eventDtoRequest.getAnnotation().length() > 2000) {
                throw new ValidationException("Аннотация не должна быть: \n" +
                        "- пустой \n" +
                        "- меньше 20 символов или больше 2000");
            } else {
                oldEvent.setAnnotation(eventDtoRequest.getAnnotation());
            }
        }
        if (eventDtoRequest.getLocation() != null) {
            oldEvent.setLat(eventDtoRequest.getLocation().getLat());
            oldEvent.setLon(eventDtoRequest.getLocation().getLon());
        }
        if (eventDtoRequest.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(eventDtoRequest.getParticipantLimit());
        }
        if (eventDtoRequest.getTitle() != null && !eventDtoRequest.getTitle().trim().isEmpty()) {
            if (eventDtoRequest.getTitle().length() < 3 || eventDtoRequest.getTitle().length() > 120) {
                throw new ValidationException("Не валидная длина заголовка!");
            }
            oldEvent.setTitle(eventDtoRequest.getTitle());
        }
        if (eventDtoRequest.getPaid() != null) {
            oldEvent.setPaid(eventDtoRequest.getPaid());
        }
        if (eventDtoRequest.getRequestModeration() != null) {
            oldEvent.setRequestModeration(eventDtoRequest.getRequestModeration());
        }
        if (eventDtoRequest.getStateAction() != null) {
            if (eventDtoRequest.getStateAction().equals("PUBLISH_EVENT")) {
                if (oldEvent.getState().equals(State.CANCELED) || oldEvent.getState().equals(State.PUBLISHED)) {
                    throw new ConflictException("Событие уже опубликовано или отменено!");
                }
                oldEvent.setState(State.PUBLISHED);
                if (!oldEvent.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
                    throw new ConflictException("Дата начала события должна быть не ранее чем за час от даты публикации!");
                }
                oldEvent.setPublishedOn(LocalDateTime.now());
            } else {
                if (oldEvent.getState().equals(State.PUBLISHED)) {
                    throw new ConflictException("Событие уже нельзя отклонить!");
                }
                oldEvent.setState(State.CANCELED);
            }
        }
        return EventMapper.toEventDto(repository.save(oldEvent));
    }
}