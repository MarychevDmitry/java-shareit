package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.UserValidationException;
import org.apache.commons.lang3.StringUtils;
import ru.practicum.shareit.user.dto.UserDto;

import static ru.practicum.shareit.utilitary.Constants.EMAIL_REGEX_PATTERN;
import static ru.practicum.shareit.utilitary.PatternValidator.isPatternMatches;

public class UserValidator {
    public static boolean isUserValid(User user) {
        if (user.getId() != null && user.getId() <= 0) {
            throw new UserValidationException("Id validation error.");
        } else if (StringUtils.isBlank(user.getName())) {
            throw new UserValidationException("Name validation error.");
        } else if (!isPatternMatches(user.getEmail(), EMAIL_REGEX_PATTERN)) {
            throw new UserValidationException("Email validation error.");
        } else {
            return true;
        }
    }

    public static boolean isUserDtoValid(UserDto userDto) {
        if (userDto.getId() != null && userDto.getId() <= 0) {
            throw new UserValidationException("Id validation error.");
        } else if (StringUtils.isBlank(userDto.getName())) {
            throw new UserValidationException("Name validation error.");
        } else if (!isPatternMatches(userDto.getEmail(), EMAIL_REGEX_PATTERN)) {
            throw new UserValidationException("Email validation error.");
        } else {
            return true;
        }
    }
}
