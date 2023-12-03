package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAlreadyExistException extends IllegalArgumentException {
    public UserAlreadyExistException(String message) {
        super(message);
        log.error("ERROR: " + message);
    }
}