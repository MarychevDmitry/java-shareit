package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.ItemValidator.isItemValid;

@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {

    public Map<Long, Item> items = new HashMap<>();
    private Long currentId = 0L;

    @Override
    public Item getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException(itemId);
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(toList());
    }

    @Override
    public List<Item> getItemsBySearchQuery(String text) {
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            searchItems = items.values().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(text) ||
                            item.getDescription().toLowerCase().contains(text))
                    .collect(toList());
        }
        return searchItems;
    }

    @Override
    public Item create(Item item) {
        isItemValid(item);
        item.setId(++currentId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new ItemNotFoundException(item.getId());
        }
        if (item.getName() == null) {
            item.setName(items.get(item.getId()).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(item.getId()).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(item.getId()).getAvailable());
        }
        isItemValid(item);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item delete(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException(itemId);
        }
        return items.remove(itemId);
    }

    @Override
    public void deleteItemsByOwner(Long ownerId) {
        List<Long> deleteIds = items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .map(Item::getId)
                .collect(toList());
        for (Long deleteId : deleteIds) {
            items.remove(deleteId);
        }
    }
}
