package ru.practicum.shareit.handler.exception;

import lombok.Getter;

@Getter
public class StateValidationException extends RuntimeException {
    private final String message;

    public StateValidationException(String message) {
        this.message = message;
    }
}