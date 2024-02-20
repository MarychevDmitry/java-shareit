package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;

@Slf4j
public class CommentValidationException extends ValidationException {
    public CommentValidationException(String message) {
        super(message);
        log.error("ERROR: " + message);
    }
}