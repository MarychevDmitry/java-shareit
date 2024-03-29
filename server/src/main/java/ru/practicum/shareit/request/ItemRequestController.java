package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithItem;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.utilitary.Constants.HEADER_USER_ID;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoResponse createRequest(@RequestHeader(HEADER_USER_ID) Long requesterId,
                                                @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST: request to the endpoint was received: '/requests' add new requests");
        return itemRequestService.createItemRequest(itemRequestDto, requesterId);
    }

    @GetMapping
    public List<RequestDtoResponseWithItem> getPrivateRequests(
            @RequestHeader(HEADER_USER_ID) Long requesterId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET: request to the endpoint was received: '/requests' get private requests");
        return itemRequestService.getPrivateRequests(PageRequest.of(from / size, size)
                .withSort(Sort.by("created").descending()), requesterId);
    }

    @GetMapping("all")
    public List<RequestDtoResponseWithItem> getOtherRequests(
            @RequestHeader(HEADER_USER_ID) Long requesterId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET: request to the endpoint was received: '/requests' get other requests");
        return itemRequestService.getOtherRequests(PageRequest.of(
                from / size, size, Sort.by(Sort.Direction.DESC, "created")), requesterId);
    }

    @GetMapping("{requestId}")
    public RequestDtoResponseWithItem getItemRequest(
            @RequestHeader(HEADER_USER_ID) Long userId,
            @PathVariable Long requestId) {
        log.info("GET: request to the endpoint was received: '/requests' get item requests by id");
        return itemRequestService.getItemRequest(userId, requestId);
    }
}