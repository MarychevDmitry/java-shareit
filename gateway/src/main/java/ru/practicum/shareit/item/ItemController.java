package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.common.Header;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@Validated
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(Header.userIdHeader) @Min(1) Long ownerId,
                                                        @PathVariable @Min(1) Long itemId) {
        log.info("GET: request was received to the endpoint: '/items' to receive an item with ID={}", itemId);
        return itemClient.getItemById(ownerId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(
            @RequestHeader(Header.userIdHeader) @Min(1) Long ownerId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("GET: request was received to the endpoint: '/items' to receive all the owner's items with ID={}", ownerId);
        return itemClient.getItemsByOwner(ownerId, from, size);
    }

    @GetMapping("search")
    public ResponseEntity<Object> searchItems(
            @RequestHeader(Header.userIdHeader) @Min(1) Long userId,
            @RequestParam String text,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        log.info("GET: request was received to the endpoint: '/items/search' to search for an item with text={}", text);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(Header.userIdHeader) @Min(1) Long ownerId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("POST: request was received to the endpoint: '/items' to add an item by the owner with ID={}", ownerId);
        return itemClient.createItem(ownerId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(Header.userIdHeader) @Min(1) Long ownerId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable @Min(1) Long itemId) {
        log.info("PATCH: request was received to the endpoint: '/items' to update the item with ID={}", itemId);
        return itemClient.updateItem(ownerId, itemDto, itemId);
    }

    @DeleteMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(Header.userIdHeader)
                                             @PathVariable @Min(1) Long itemId) {
        log.info("DELETE: request was received to the endpoint: '/items' to delete an item with ID={}", itemId);
        return itemClient.deleteItem(itemId);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable @Min(1) Long itemId,
                                             @RequestHeader(Header.userIdHeader) @Min(1) Long userId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("POST: request was received to the endpoint: '/{itemId}/comment' user {} add comment for Item {}", userId, itemId);
        return itemClient.addComment(itemId, userId, commentDto);
    }

}