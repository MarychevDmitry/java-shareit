package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.entity.User;

import java.util.List;

public interface UserDao {
    List<User> getUsers();

    User getById(Long userId);

    User create(User user);

    User update(User user);

    User delete(Long userId);
}
