package ru.practicum.shareit.exception;

public class IncorrectBookingException extends RuntimeException {
    public IncorrectBookingException(String message) {
        super(message);
    }
}