package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.shareit.user.UserValidator.isUserValid;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

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
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("User with ID = " + userId + " not found!");
        }
        return users.get(userId);
    }

    @Override
    public User create(User user) {
        if (users.values().stream().anyMatch(s -> s.getEmail().equals(user.getEmail()))) {
            throw new UserAlreadyExistException("User with Email = " + user.getEmail() + " already exist!");
        }
        user.setId(generateId());
        isUserValid(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("User with ID = " + user.getId() + " not found!");
        }
        if (user.getName() == null) {
            user.setName(users.get(user.getId()).getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(users.get(user.getId()).getEmail());
        }
        if (users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .allMatch(u -> u.getId().equals(user.getId()))) {
                isUserValid(user);
                users.put(user.getId(), user);
        } else {
            throw new UserAlreadyExistException("User with Email = " + user.getEmail() + " already exist!");
        }
        return user;
    }

    @Override
    public User delete(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("User with ID = " + userId + " not found!");
        }
        return users.remove(userId);
    }
}
