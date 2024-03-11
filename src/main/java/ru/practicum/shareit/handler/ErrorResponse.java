package ru.practicum.shareit.handler;

import lombok.Data;
import lombok.NonNull;

@Data
public class ErrorResponse {
    @NonNull
    private String error;
    private String description;
}