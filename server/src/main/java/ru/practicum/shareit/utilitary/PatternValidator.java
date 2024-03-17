package ru.practicum.shareit.utilitary;

import java.util.regex.Pattern;

public class PatternValidator {

    public static boolean isPatternMatches(String string, String regexPattern) {
        if (string == null || regexPattern == null) {
            return false;
        }
        return Pattern.compile(regexPattern)
                .matcher(string)
                .matches();
    }
}