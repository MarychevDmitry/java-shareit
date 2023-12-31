package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс описывающий модель User
 */

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}
