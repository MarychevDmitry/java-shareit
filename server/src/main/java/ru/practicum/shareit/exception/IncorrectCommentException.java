package ru.practicum.shareit.exception;

public class IncorrectCommentException extends RuntimeException {
    public IncorrectCommentException(String message) {
        super(message);
    }
}