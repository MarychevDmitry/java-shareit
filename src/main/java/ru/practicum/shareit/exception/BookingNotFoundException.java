package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingNotFoundException extends IllegalArgumentException {
    public BookingNotFoundException(String message) {
        super(message);
    }

    public BookingNotFoundException(Long id) {
        super("ERROR: Booking with ID = " + id + " not found!");
    }
}
