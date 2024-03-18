package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("GET: request to the endpoint was received: '/users' to receive users");
        return userClient.getUsers();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") @Min(1) Long userId) {
        log.info("GET: request to the endpoint was received: '/users' to receive the user with ID={}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST: request to the endpoint was received: '/users' to add a user");
        return userClient.createUser(userDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable("id") Long userId) {
        log.info("PATCH: request to the endpoint was received: '/users' to update a user with ID={}", userId);
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@Min(1) @PathVariable("id") Long userId) {
        log.info("DELETE: request to the endpoint was received: '/users' to delete a user with ID={}", userId);
        return userClient.deleteUser(userId);
    }
}