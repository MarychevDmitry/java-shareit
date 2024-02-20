package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.entity.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    private Long generateId() {
        return ++id;
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList(items.values());
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getItemByUser(Long userId) {
        return items.values().stream()
                .filter(item -> item.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String query) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {

        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long itemId, Item oldItem) {
        Item newItem = items.get(itemId);
        items.put(itemId, newItem);
        return newItem;
    }

    @Override
    public void delete(Long itemId) {
        items.remove(itemId);
    }
}
