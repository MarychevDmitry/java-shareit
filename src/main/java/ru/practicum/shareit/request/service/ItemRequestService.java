package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithItem;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId);

    List<RequestDtoResponseWithItem> getPrivateRequests(PageRequest pageRequest, Long requesterId);

    List<RequestDtoResponseWithItem> getOtherRequests(PageRequest pageRequest, Long requesterId);

    RequestDtoResponseWithItem getItemRequest(Long userId, Long requestId);
}