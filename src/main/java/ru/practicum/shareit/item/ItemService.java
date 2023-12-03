package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemService {

    private ItemStorage itemStorage;
    private ItemMapper mapper;

    @Autowired
    public ItemService(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.mapper = itemMapper;
    }

    public ItemDto getItemById(Long id) {
        return mapper.toItemDto(itemStorage.getItemById(id));
    }

    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemStorage.getItemsByOwner(ownerId).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }

    public List<ItemDto> getItemsBySearchQuery(String text) {
        text = text.toLowerCase();
        return itemStorage.getItemsBySearchQuery(text).stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }

    public ItemDto create(ItemDto itemDto, Long ownerId) {
        return mapper.toItemDto(itemStorage.create(mapper.toItem(itemDto, ownerId)));
    }

    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemStorage.getItemById(itemId);
        if (!oldItem.getOwnerId().equals(ownerId)) {
            throw new ItemNotFoundException("User does not have such a item!");
        }
        return mapper.toItemDto(itemStorage.update(mapper.toItem(itemDto, ownerId)));
    }

    public ItemDto delete(Long itemId, Long ownerId) {
        Item item = itemStorage.getItemById(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new ItemNotFoundException("User does not have such a item!");
        }
        return mapper.toItemDto(itemStorage.delete(itemId));
    }

    public void deleteItemsByOwner(Long ownerId) {
        itemStorage.deleteItemsByOwner(ownerId);
    }
}
