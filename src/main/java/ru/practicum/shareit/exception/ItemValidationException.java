package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;

@Slf4j
public class ItemValidationException extends ValidationException {
    public ItemValidationException(String message) {
        super(message);
    }
}
