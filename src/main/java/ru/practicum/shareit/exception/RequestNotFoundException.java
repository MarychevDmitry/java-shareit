package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestNotFoundException extends IllegalArgumentException {

    public RequestNotFoundException(String message) {
        super(message);
        log.error("ERROR: " + message);
    }

    public RequestNotFoundException(Long id) {
        super("ERROR: Request with ID = " + id + " not found!");
        log.error("ERROR: Request with ID = " + id + " not found!");
    }
}
