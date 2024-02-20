package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNotFoundException extends IllegalArgumentException {
    public ItemNotFoundException(String message) {
        super(message);
        log.error("ERROR: " + message);
    }

    public ItemNotFoundException(Long id) {
        super("ERROR: Item with ID = " + id + " not found!");
        log.error("ERROR: Item with ID = " + id + " not found!");
    }
}
