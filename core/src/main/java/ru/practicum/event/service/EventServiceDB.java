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
import ru.practicum.exception.ResourceNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
            categoriesQuery.addAll(categoryRepository.findAll());
        } else {
            for (Long id : categories) {
                categoriesQuery.add(categoryRepository.findById(Math.toIntExact(id))
                        .orElseThrow(() -> new ValidationException("События с " + id + "нет!")));
            }
        }
        if (paid == null) {
            paidQuery.add(true);
            paidQuery.add(false);
        } else {
            paidQuery.add(paid);
        }
        if (text.isEmpty()) {
            if (onlyAvailable) {
                statsClient.postStatistic(request.getRemoteAddr(), request.getRequestURI(), LocalDateTime.now());
                return EventMapper.toEventDtoList(repository
                        .findByNoTextQueryOnlyAvailable(categoriesQuery, paidQuery, startQuery, endQuery,
                                PageRequest.of(from, size, Sort.by(sortQuery))).toList());
            } else {
                statsClient.postStatistic(request.getRemoteAddr(), request.getRequestURI(), LocalDateTime.now());
                return EventMapper.toEventDtoList(repository
                        .findByNoTextQueryNoOnlyAvailable(categoriesQuery, paidQuery, startQuery, endQuery,
                                PageRequest.of(from, size, Sort.by(sortQuery))).toList());
            }
        } else {
            if (onlyAvailable) {
                statsClient.postStatistic(request.getRemoteAddr(), request.getRequestURI(), LocalDateTime.now());
                text = "%" + text.toLowerCase() + "%";
                return EventMapper.toEventDtoList(repository
                        .findByAllQueryOnlyAvailable(text, categoriesQuery, paidQuery, startQuery, endQuery,
                                PageRequest.of(from, size, Sort.by(sortQuery))).toList());
            } else {
                statsClient.postStatistic(request.getRemoteAddr(), request.getRequestURI(), LocalDateTime.now());
                text = "%" + text.toLowerCase() + "%";
                return EventMapper.toEventDtoList(repository
                        .findByAllQueryNoOnlyAvailable(text, categoriesQuery, paidQuery, startQuery, endQuery,
                                PageRequest.of(from, size, Sort.by(sortQuery))).toList());
            }
        }
    }

    @Override
    public EventDto getEvent(Integer id) {
        Event event = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Такого события нет!"));
        if (event.getViews() == null) {
            event.setViews(1);
        } else {
            event.setViews(event.getViews() + 1);
        }
        return EventMapper.toEventDto(repository.save(event));
    }

    @Override
    public List<EventDto> getAdminEvents(List<Long> users, List<State> states, List<Long> categories,
                                         String start, String end, Integer from, Integer size) {
        List<User> usersQuery = new ArrayList<>();
        List<Category> categoriesQuery = new ArrayList<>();
        List<State> statesQuery = new ArrayList<>();
        if (users.isEmpty()) {
            usersQuery = userRepository.findAll();
        } else {
            for (Long id : users) {
                usersQuery.add(userRepository.findById(Math.toIntExact(id)).
                        orElseThrow(() -> new ValidationException("Пользователя с " + id + " не существует!")));
            }
        }
        if (categories.isEmpty()) {
            categoriesQuery = categoryRepository.findAll();
        } else {
            for (Long id : categories) {
                categoriesQuery.add(categoryRepository.findById(Math.toIntExact(id)).
                        orElseThrow(() -> new ValidationException("Категории с " + id + " не существует!")));
            }
        }
        if (states.isEmpty()) {
            statesQuery.add(State.WAIT);
            statesQuery.add(State.CANCELED);
            statesQuery.add(State.PUBLISHED);
        }
        return EventMapper.toEventDtoList(repository.findByAdminQuery(usersQuery, statesQuery, categoriesQuery,
                LocalDateTime.parse(start, formatter), LocalDateTime.parse(end, formatter),
                PageRequest.of(from, size)).toList());
    }

    @Override
    public EventDto patchAdminEvent(Integer eventId, EventDtoRequest eventDtoRequest) {
        Event oldEvent = repository.findById(eventId).orElseThrow(() -> new ValidationException("Такого события нет!"));
        if (!eventDtoRequest.getAnnotation().isEmpty() || eventDtoRequest.getAnnotation() != null) {
            oldEvent.setAnnotation(eventDtoRequest.getAnnotation());
        }
        if (eventDtoRequest.getCategory() != null) {
            oldEvent.setCategory(categoryRepository.findById(eventDtoRequest.getCategory()).
                    orElseThrow(() -> new ValidationException("Такой категории не существует!")));
        }
        if (!eventDtoRequest.getDescription().isEmpty() || eventDtoRequest.getDescription() != null) {
            oldEvent.setDescription(eventDtoRequest.getDescription());
        }
        if (eventDtoRequest.getEventDate() != null) {
            oldEvent.setEventDate(eventDtoRequest.getEventDate());
        }
        if (eventDtoRequest.getLocation() != null) {
            oldEvent.setLat(eventDtoRequest.getLocation().getLat());
            oldEvent.setLon(eventDtoRequest.getLocation().getLon());
        }
        if (eventDtoRequest.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(eventDtoRequest.getParticipantLimit());
        }
        if (eventDtoRequest.getTitle() != null && !eventDtoRequest.getTitle().isEmpty()) {
            oldEvent.setTitle(eventDtoRequest.getTitle());
        }
        oldEvent.setPaid(eventDtoRequest.isPaid());
        oldEvent.setRequestModeration(eventDtoRequest.isRequestModeration());
        if (eventDtoRequest.getStateAction().equals("PUBLISH_EVENT")) {
            oldEvent.setState(State.PUBLISHED);
            oldEvent.setPublishedOn(LocalDateTime.now());
        } else {
            oldEvent.setState(State.CANCELED);
        }
        return EventMapper.toEventDto(repository.save(oldEvent));
    }
}