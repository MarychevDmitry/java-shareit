package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    List<UserDto> getUsers();

    UserDto getUserById(Long userId);

    UserDto create(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    void delete(Long userId);
}
