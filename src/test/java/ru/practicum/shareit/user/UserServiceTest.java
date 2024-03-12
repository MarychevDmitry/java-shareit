package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyUsedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestPropertySource(properties = {"db.name=test"})
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceTest {

    private final UserService userService;
    private static UserDto user1;
    private static UserDto user2;
    private static UserDto updateUser1;

    @BeforeEach
    public void setUp() {
        user1 = UserDto.builder()
                .name("test name")
                .email("test@test.ru")
                .build();
        user2 = UserDto.builder()
                .name("test name 2")
                .email("test2@test.ru")
                .build();
    }

    @Test
    public void createAndGetUser() {
        var savedUser = userService.create(user1);
        var findUser = userService.getUserById(1L);

        assertThat(savedUser).usingRecursiveComparison().isEqualTo(findUser);
    }

    @Test
    public void createUserWithDuplicateEmail() {
        userService.create(user1);

        assertThatThrownBy(() -> userService.create(user1)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void getNotExistUserById() {
        assertThatThrownBy(() -> userService.getUserById(2L)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void getEmptyUsersList() {
        List<UserDto> users = userService.getUsers();

        assertThat(users.isEmpty());
    }

    @Test
    public void getUsersList() {
        var savedUser1 = userService.create(user1);
        var savedUser2 = userService.create(user2);
        var findUsers = userService.getUsers();

        assertThat(findUsers).element(0).usingRecursiveComparison().isEqualTo(savedUser1);
        assertThat(findUsers).element(1).usingRecursiveComparison().isEqualTo(savedUser2);
    }

    @Test
    public void updateUser() {
        updateUser1 = UserDto.builder()
                .name("update name")
                .email("update-email@test.ru")
                .build();

        userService.create(user1);
        userService.update(1L, updateUser1);
        var updatedUser1 = userService.getUserById(1L);

        assertThat(updatedUser1.getName()).isEqualTo(updateUser1.getName());
        assertThat(updatedUser1.getEmail()).isEqualTo(updateUser1.getEmail());
    }

    @Test
    public void updateUserName() {
        updateUser1 = UserDto.builder()
                .email("update-email@test.ru")
                .build();

        userService.create(user1);
        userService.update(1L, updateUser1);
        var updatedUser1 = userService.getUserById(1L);

        assertThat(updatedUser1.getName()).isEqualTo(user1.getName());
        assertThat(updatedUser1.getEmail()).isEqualTo(updatedUser1.getEmail());
    }

    @Test
    public void updateUserEmail() {
        updateUser1 = UserDto.builder()
                .name("update name")
                .build();

        userService.create(user1);
        userService.update(1L, updateUser1);
        var updatedUser1 = userService.getUserById(1L);

        assertThat(updatedUser1.getName()).isEqualTo(updateUser1.getName());
        assertThat(updatedUser1.getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    @Transactional(propagation = Propagation.SUPPORTS)
    public void updateUserDuplicateEmail() {
        updateUser1 = UserDto.builder()
                .email(user1.getEmail())
                .build();

        userService.create(user1);
        userService.create(user2);

        assertThatThrownBy(() -> userService.update(2L, updateUser1)).isInstanceOf(EmailAlreadyUsedException.class);
    }

    @Test
    public void deleteUserById() {
        var savedUser = userService.create(user1);

        userService.delete(savedUser.getId());

        assertThatThrownBy(() -> userService.getUserById(savedUser.getId())).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void deleteUserByNotExistId() {
        assertThatThrownBy(() -> userService.delete(1L)).isInstanceOf(UserNotFoundException.class);
    }
}