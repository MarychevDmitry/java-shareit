package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.entity.User;


import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestPropertySource(properties = {"db.name=test"})
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestServiceTest {

    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private User user1;
    private User user2;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setName("test name");
        user1.setEmail("test@test.ru");
        user2 = new User();
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");
        itemRequestDto = ItemRequestDto.builder()
                .description("test description")
                .build();
    }

    @Test
    @Transactional
    public void createItemRequest() {
        userRepository.save(user1);

        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        var findRequest = itemRequestService.getItemRequest(user1.getId(), savedRequest.getId());

        assertThat(savedRequest).usingRecursiveComparison().ignoringFields("items", "created")
                .isEqualTo(findRequest);
    }

    @Test
    public void createItemRequestWhenRequesterNotFound() {
        userRepository.save(user1);

        assertThatThrownBy(() -> itemRequestService.createItemRequest(itemRequestDto, 99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @Transactional
    public void getPrivateRequest() {
        userRepository.save(user1);
        userRepository.save(user2);

        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user2.getId());
        var privateRequests = itemRequestService.getPrivateRequests(PageRequest.of(0, 2), user2.getId());
        var findRequest = itemRequestService.getItemRequest(user2.getId(), savedRequest.getId());

        assertThat(privateRequests.get(0)).usingRecursiveComparison().isEqualTo(findRequest);
    }

    @Test
    public void getPrivateRequestWhenRequesterNotExistingRequests() {
        userRepository.save(user1);

        assertThatThrownBy(() -> itemRequestService.getPrivateRequests(PageRequest.of(0, 2), 55L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @Transactional
    public void getOtherRequests() {
        userRepository.save(user1);
        userRepository.save(user2);

        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        var findRequest = itemRequestService.getItemRequest(user1.getId(), savedRequest.getId());
        var otherRequest = itemRequestService.getOtherRequests(PageRequest.of(0, 2), user2.getId());

        assertThat(otherRequest.get(0)).usingRecursiveComparison().isEqualTo(findRequest);
    }

    @Test
    public void getOtherRequestsWhenRequesterNotFound() {
        userRepository.save(user1);

        itemRequestService.createItemRequest(itemRequestDto, user1.getId());

        assertThatThrownBy(() -> itemRequestService.getOtherRequests(PageRequest.of(0, 2), 50L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void getItemRequestWhenUserNotFound() {
        userRepository.save(user1);

        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());

        assertThatThrownBy(() -> itemRequestService.getItemRequest(50L, savedRequest.getId()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void getItemRequestWhenRequestNotFound() {
        userRepository.save(user1);

        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());

        assertThatThrownBy(() -> itemRequestService.getItemRequest(1L, 50L))
                .isInstanceOf(NotFoundException.class);
    }
}