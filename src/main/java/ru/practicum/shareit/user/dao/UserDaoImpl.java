package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.entity.User;

import java.util.*;

import static ru.practicum.shareit.user.UserValidator.isUserValid;

@Repository
public class UserDaoImpl implements UserDao {

    public Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    private Long generateId() {
        return ++id;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(userId);
        }
        return users.get(userId);
    }

    @Override
    public User create(User user) {
        if (users.values().stream().anyMatch(s -> s.getEmail().equals(user.getEmail()))) {
            throw new UserAlreadyExistException(user.getEmail());
        }
        user.setId(generateId());
        isUserValid(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(user.getId());
        }
        User userToUpdate = users.get(user.getId());
        Optional.ofNullable(user.getName()).ifPresent(userToUpdate::setName);
        if (users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .allMatch(u -> u.getId().equals(user.getId()))) {
            Optional.ofNullable(user.getEmail()).ifPresent(userToUpdate::setEmail);
            isUserValid(userToUpdate);
            users.put(userToUpdate.getId(), userToUpdate);
        } else {
            throw new UserAlreadyExistException(userToUpdate.getEmail());
        }
        return userToUpdate;
    }

    @Override
    public User delete(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(userId);
        }
        return users.remove(userId);
    }
}
