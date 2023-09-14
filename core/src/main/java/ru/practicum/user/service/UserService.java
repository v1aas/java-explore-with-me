package ru.practicum.user.service;

import ru.practicum.comment.model.CommentaryDto;
import ru.practicum.event.model.EventDto;
import ru.practicum.event.model.EventDtoRequest;
import ru.practicum.request.model.AllUserRequestFormat;
import ru.practicum.request.model.AllUserRequestResponse;
import ru.practicum.request.model.UserRequestDto;
import ru.practicum.user.model.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size);

    UserDto createUser(UserDto user);

    UserDto updateUser(Integer id, UserDto user);

    UserDto deleteUser(Integer id);

    EventDto createEventForUser(Integer userId, EventDtoRequest event);

    List<EventDto> getEventsForUser(Integer userId, Integer from, Integer size);

    EventDto getEventForUser(Integer userId, Integer eventId);

    EventDto updateEventForOwner(Integer userId, Integer eventId, EventDtoRequest event);

    UserRequestDto createRequestForEvent(Integer userId, Integer eventId);

    List<UserRequestDto> getRequestsForUser(Integer userId);

    UserRequestDto cancelUserRequestForEvent(Integer userId, Integer requestId);

    List<UserRequestDto> getAllRequestsForOwnerEvent(Integer userId, Integer eventId);

    AllUserRequestResponse changeStatusRequestForEvent(Integer userId, Integer eventId, AllUserRequestFormat request);

    CommentaryDto createComment(Integer userId, Integer eventId, CommentaryDto commentaryDto);

    CommentaryDto updateComment(Integer userId, Integer commentId, CommentaryDto commentaryDto);

    void deleteComment(Integer userId, Integer commentId);
}