package ru.practicum.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AllUserRequestResponse {
    private List<UserRequestDto> confirmedRequests;
    private List<UserRequestDto> rejectedRequests;
}