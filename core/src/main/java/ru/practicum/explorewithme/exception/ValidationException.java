package ru.practicum.explorewithme.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String mess) {
        super(mess);
    }
}