package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Status;
import ru.practicum.request.model.UserRequest;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRequestRepository extends JpaRepository<UserRequest, Integer> {

    List<UserRequest> findAllByRequester(User requester);

    List<UserRequest> findAllByEvent(Event event);

    List<UserRequest> findAllByStatusAndRequester(Status status, User requester);
}