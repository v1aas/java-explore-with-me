package ru.practicum.request.mapper;

import ru.practicum.request.model.UserRequest;
import ru.practicum.request.model.UserRequestDto;

import java.util.ArrayList;
import java.util.List;

public class UserRequestMapper {

    public static UserRequestDto toRequestDto(UserRequest userRequest) {
        return new UserRequestDto(userRequest.getId(),
                userRequest.getCreated(),
                userRequest.getEvent().getId(),
                userRequest.getRequester().getId(),
                userRequest.getStatus());
    }

    public static List<UserRequestDto> toRequestDtoList(List<UserRequest> list) {
        List<UserRequestDto> userRequestDtoList = new ArrayList<>();
        for (UserRequest request : list) {
            userRequestDtoList.add(toRequestDto(request));
        }
        return userRequestDtoList;
    }
}