package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyUsedException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.user.dto.UserMapper.fromUserDto;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDtoList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<UserDto> getUsers() {
        return toUserDtoList(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(fromUserDto(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User user = fromUserDto(userDto);
        user.setId(userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        User userToUpdate = userRepository.findById(userId).get();

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }

        if (user.getEmail() != null) {
            List<User> findEmail = userRepository.findByEmail(user.getEmail());

            if (!findEmail.isEmpty() && !Objects.equals(findEmail.get(0).getId(), userId)) {
                throw new EmailAlreadyUsedException(user.getEmail());
            }
            userToUpdate.setEmail(user.getEmail());
        }

        userRepository.save(userToUpdate);
        return UserMapper.toUserDto(userToUpdate);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.delete(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId)));
    }
}
