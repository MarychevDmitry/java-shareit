package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItemById(Long id);

    List<ItemDto> getItemsByOwner(Long ownerId);

    List<ItemDto> getItemsBySearchQuery(String text);

    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(ItemDto itemDto, Long ownerId, Long itemId);

    ItemDto delete(Long itemId, Long ownerId);
    void deleteItemsByOwner(Long ownerId);
}
