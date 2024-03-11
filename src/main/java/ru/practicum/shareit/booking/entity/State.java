package ru.practicum.shareit.booking.entity;

import ru.practicum.shareit.exception.IncorrectStatusException;

public enum State {

    ALL,

    CURRENT,

    PAST,

    FUTURE,

    WAITING,

    REJECTED;

    public static State getEnumValue(String state) {
        try {
            return State.valueOf(state);
        } catch (Exception e) {
            throw new IncorrectStatusException("Unknown state: " + state);
        }
    }
}