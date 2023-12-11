package ru.practicum.shareit.utilitary;

public class Constants {
    public static final String EMAIL_REGEX_PATTERN = "[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})";

    public static final String OWNER_HEADER = "X-Sharer-User-Id";
}
