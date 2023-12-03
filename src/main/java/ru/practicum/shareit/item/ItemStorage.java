package ru.practicum.shareit.item;

import java.util.List;

public interface ItemStorage {

    Item getItemById(Long itemId);

    List<Item> getItemsByOwner(Long ownerId);

    List<Item> getItemsBySearchQuery(String text);

    Item create(Item item);

    Item update(Item item);

    Item delete(Long userId);

    void deleteItemsByOwner(Long ownerId);
}