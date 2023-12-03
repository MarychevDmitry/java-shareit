package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User getUserById(Long userId);

    User create(User user);

    User update(User user);

    User delete(Long userId);
}