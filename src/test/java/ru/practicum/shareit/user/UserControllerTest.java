package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserControllerTest {

    private final ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    private final MockMvc mvc;
    private static UserDto userDtoWithId;
    private static UserDto userDto;
    private static UserDto userDtoUpdate;

    @BeforeEach
    public void setUp() {
        userDtoWithId = UserDto.builder()
                .id(1L)
                .name("test name")
                .email("test@test.ru")
                .build();
        userDto = UserDto.builder()
                .name("test name")
                .email("test@test.ru")
                .build();
        userDtoUpdate = UserDto.builder()
                .name("test name")
                .email("test@test.ru")
                .build();
    }

    @Test
    public void createUser() throws Exception {
        when(userService.create(any(UserDto.class))).thenReturn(userDtoWithId);

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userDtoWithId)));
    }

    @Test
    public void createUserWithIncorrectName() throws Exception {
        UserDto userDtoWithIncorrectName = UserDto.builder()
                .name("  incorrect name")
                .build();

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoWithIncorrectName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(userService, times(0)).create(any(UserDto.class));
    }

    @Test
    public void createUserWithEmptyName() throws Exception {
        UserDto userDtoWithIncorrectName = UserDto.builder()
                .name("     ")
                .build();

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoWithIncorrectName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(userService, times(0)).create(any(UserDto.class));
    }

    @Test
    public void createUserWithIncorrectEmail() throws Exception {
        UserDto userDtoWithIncorrectEmail = UserDto.builder()
                .name("test name")
                .email("incorrect-email@.ru")
                .build();

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoWithIncorrectEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(userService, times(0)).create(any(UserDto.class));
    }

    @Test
    public void getUserById() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userDtoWithId);

        mvc.perform(get("/users/1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userDtoWithId)));
    }

    @Test
    public void getUserByNotExistingId() throws Exception {
        when(userService.getUserById(anyLong())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mvc.perform(get("/users/1"))
                .andDo(print())
                .andExpectAll(status().isNotFound());
    }

    @Test
    public void getUserByIncorrectId() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userDtoWithId);

        mvc.perform(get("/users/-1"))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
    }

    @Test
    public void getUsers() throws Exception {
        List<UserDto> userList = List.of(userDtoWithId);
        when(userService.getUsers()).thenReturn(userList);

        mvc.perform(get("/users"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userList)));
    }

    @Test
    public void updateUser() throws Exception {
        when(userService.update(anyLong(), any(UserDto.class))).thenReturn(userDtoWithId);

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(userDtoWithId)));
    }

    @Test
    public void deleteUser() throws Exception {
        mvc.perform(delete("/users/1"))
                .andDo(print())
                .andExpectAll(status().isOk());
        verify(userService, times(1)).delete(1L);
    }
}