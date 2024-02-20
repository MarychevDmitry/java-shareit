package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getById(Long itemId);

    ItemDto getItemsByOwner(Long itemId, Long ownerId);


    List<ItemDto> getItemsByOwner(Long ownerId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    ItemDto create(ItemDto itemDto, Long ownerId);

    CommentDto addComment(Long ownerId, Long itemId, CommentDto commentDto);

    ItemDto update(ItemDto itemDto, Long itemId, Long ownerId);

    void delete(Long itemId);
}
