package ru.practicum.shareit.user.dto;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User fromUserDto(UserDto userDto) {
        return ru.practicum.shareit.user.entity.User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static List<UserDto> toUserDtoList(Iterable<User> users) {
        List<UserDto> result = new ArrayList<>();
        for (ru.practicum.shareit.user.entity.User user : users) {
            result.add(toUserDto(user));
        }
        return result;
    }
}
