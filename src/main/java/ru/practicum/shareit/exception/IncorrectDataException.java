package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IncorrectDataException extends RuntimeException {
    public IncorrectDataException(String message) {
        super(message);
    }
}
