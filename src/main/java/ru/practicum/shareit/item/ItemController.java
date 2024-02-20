package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.utilitary.Constants;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.item.ItemValidator.isItemDtoValid;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String HEADER_USER_ID = Constants.HEADER_USER_ID;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(HEADER_USER_ID) Long ownerId,
                               @PathVariable Long itemId) {
        log.info("GET: request was received to the endpoint: '/items' to receive an item with ID={}", itemId);
        return itemService.getItemsByOwner(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(HEADER_USER_ID) Long ownerId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET: request was received to the endpoint: '/items' to receive all the owner's items with ID={}", ownerId);
        return itemService.getItemsUser(ownerId, from, size);
    }

    @GetMapping("search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET: request was received to the endpoint: '/items/search' to search for an item with text={}", text);
        return itemService.search(text, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER_USER_ID) Long ownerId, @RequestBody ItemDto itemDto) {
        log.info("POST: request was received to the endpoint: '/items' to add an item by the owner with ID={}", ownerId);
        isItemDtoValid(itemDto);
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(HEADER_USER_ID) Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {

        log.info("PATCH: request was received to the endpoint: '/items' to update the item with ID={}", itemId);
        return itemService.updateItem(itemDto, itemId, ownerId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        log.info("DELETE: request was received to the endpoint: '/items' to delete an item with ID={}", itemId);
        itemService.delete(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HEADER_USER_ID) Long ownerId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {

        log.info("POST: request was received to the endpoint: '/{itemId}/comment' user {} add comment for Item {}", ownerId, itemId);
        return itemService.addComment(ownerId, itemId, commentDto);
    }
}