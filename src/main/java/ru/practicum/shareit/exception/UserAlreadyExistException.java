package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAlreadyExistException extends IllegalArgumentException {

    public UserAlreadyExistException(String userEmail) {
        super("ERROR: User with Email = " + userEmail + " already exist!");
    }
}