package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import javax.validation.ValidationException;

@Slf4j
public class UserValidationException extends ValidationException {
    public UserValidationException(String message) {
        super(message);
        log.error("ERROR: " + message);
    }
}

