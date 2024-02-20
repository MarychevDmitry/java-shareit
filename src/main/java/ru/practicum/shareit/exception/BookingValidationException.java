package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;

@Slf4j
public class BookingValidationException extends ValidationException {
    public BookingValidationException(String message) {
        super(message);
        log.error("ERROR: " + message);
    }
}
