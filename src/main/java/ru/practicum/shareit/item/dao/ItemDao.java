package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {

    List<Item> getItems();

    Optional<Item> getItemById(Long itemId);

    List<Item> getItemByUser(Long userId);

    List<Item> search(String query);

    Item create(Item item);

    Item update(Long itemId, Item item);

    void delete(Long itemId);
}