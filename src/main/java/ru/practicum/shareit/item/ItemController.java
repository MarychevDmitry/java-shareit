package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static ru.practicum.shareit.item.ItemValidator.isItemDtoValid;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER = "X-Sharer-User-Id";
    private ItemService itemService;
    private UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("GET request was received to the endpoint: '/items' to receive an item with ID={}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER) Long ownerId) {
        log.info("GET request was received to the endpoint: '/items' to receive all the owner's items with ID={}", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.info("GET request was received to the endpoint: '/items/search' to search for an item with text={}", text);
        return itemService.getItemsBySearchQuery(text);
    }

    @ResponseBody
    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader(OWNER) Long ownerId) {
        log.info("POST request was received to the endpoint: '/items' to add an item by the owner with ID={}", ownerId);
        ItemDto newItemDto = null;
        if (userService.getUserById(ownerId) != null) {
            newItemDto = itemService.create(itemDto, ownerId);
        }
        isItemDtoValid(newItemDto);
        return newItemDto;
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId, @RequestHeader(OWNER) Long ownerId) {
        log.info("PATCH request was received to the endpoint: '/items' to update the item with ID={}", itemId);
        ItemDto newItemDto = null;
        if (userService.getUserById(ownerId) != null) {
            newItemDto = itemService.update(itemDto, ownerId, itemId);
        }
        return newItemDto;
    }

    @DeleteMapping("/{itemId}")
    public ItemDto delete(@PathVariable Long itemId, @RequestHeader(OWNER) Long ownerId) {
        log.info("DELETE request was received to the endpoint: '/items' to delete an item with ID={}", itemId);
        return itemService.delete(itemId, ownerId);
    }
}
