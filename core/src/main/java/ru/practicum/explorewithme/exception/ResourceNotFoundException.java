package ru.practicum.explorewithme.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mess) {
        super(mess);
    }
}