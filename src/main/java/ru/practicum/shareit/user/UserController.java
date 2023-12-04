package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static ru.practicum.shareit.user.UserValidator.isUserDtoValid;

/**
 * Класс описывающий Controller для модели User
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("GET request to the endpoint was received: '/users' to receive users");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("GET request to the endpoint was received: '/users' to receive the user with ID={}", userId);
        return userService.getUserById(userId);
    }

    @ResponseBody
    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("POST request to the endpoint was received: '/users' to add a user");
        isUserDtoValid(userDto);
        return userService.create(userDto);
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("PATCH request to the endpoint was received: '/users' to update a user with ID={}", userId);
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public UserDto delete(@PathVariable Long userId) {
        log.info("DELETE request to the endpoint was received: '/users' to delete a user with ID={}", userId);
        UserDto userDto = userService.delete(userId);
        itemService.deleteItemsByOwner(userId);
        return userDto;
    }
}
