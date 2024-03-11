package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNotFoundException extends IllegalArgumentException {
    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(Long id) {
        super("ERROR: Item with ID = " + id + " not found!");
    }
}
