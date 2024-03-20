package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.dto.ItemDataForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithItem;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.List;

@Mapper
public interface ItemRequestMapper {
    ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDtoResponse mapToItemRequestDtoResponse(ItemRequest itemRequest);

    @Mapping(source = "request.id", target = "requestId")
    ItemDataForRequestDto mapToItemDataForRequestDto(Item item);

    RequestDtoResponseWithItem mapToRequestDtoResponseWithItem(ItemRequest itemRequest);

    List<RequestDtoResponseWithItem> mapToRequestDtoResponseWithItem(List<ItemRequest> itemRequests);
}