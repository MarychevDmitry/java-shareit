package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundException extends IllegalArgumentException {

    public UserNotFoundException(Long id) {
        super("ERROR: User with ID = " + id + " not found!");
        log.error("ERROR: User with ID = " + id + " not found!");
    }
}