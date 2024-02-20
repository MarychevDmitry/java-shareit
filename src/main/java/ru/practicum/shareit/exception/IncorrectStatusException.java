package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IncorrectStatusException extends RuntimeException {
    public IncorrectStatusException(String message) {
        super(message);
        log.error("ERROR: " + message);
    }
}
