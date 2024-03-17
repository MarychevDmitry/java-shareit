package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithItem;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper mapper;

    @Override
    @Transactional
    public ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        User user = userRepository.findById(requesterId).orElseThrow(() -> new UserNotFoundException(requesterId));
        ItemRequest newRequest = mapper.mapToItemRequest(itemRequestDto);
        newRequest.setRequester(user);
        newRequest.setCreated(LocalDateTime.now());
        return mapper.mapToItemRequestDtoResponse(itemRequestRepository.save(newRequest));
    }

    @Override
    @Transactional
    public List<RequestDtoResponseWithItem> getPrivateRequests(PageRequest pageRequest, Long requesterId) {
        userRepository.findById(requesterId).orElseThrow(() -> new UserNotFoundException(requesterId));
        return mapper.mapToRequestDtoResponseWithItem(itemRequestRepository.findAllByRequesterId(pageRequest, requesterId));
    }

    @Override
    @Transactional
    public List<RequestDtoResponseWithItem> getOtherRequests(PageRequest pageRequest, Long requesterId) {
        userRepository.findById(requesterId).orElseThrow(() -> new UserNotFoundException(requesterId));
        return mapper.mapToRequestDtoResponseWithItem(itemRequestRepository.findAllByRequesterIdNot(pageRequest, requesterId));
    }

    @Override
    @Transactional
    public RequestDtoResponseWithItem getItemRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
        return mapper.mapToRequestDtoResponseWithItem(
                itemRequestRepository.findById(requestId)
                        .orElseThrow(() -> new NotFoundException(String.format("No request with id=%s", requestId))));
    }
}