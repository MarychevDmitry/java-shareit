package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.entity.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.IncorrectCommentException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingShortDto;
import static ru.practicum.shareit.comment.dto.CommentMapper.toCommentDtoList;
import static ru.practicum.shareit.item.dto.ItemMapper.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Item item = fromEntityItemDto(itemDto, user);
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request with id "  + itemDto.getRequestId() + " not found")));
        }
        itemRepository.save(item);

        return toEntityItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        checkUser(ownerId);

        User user = userRepository.findById(ownerId).get();

        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(itemId);
        }
        Item item = fromEntityItemDto(itemDto, user);

        item.setId(itemId);

        Item newItem = itemRepository.findById(item.getId()).get();

        if (!newItem.getUser().equals(user)) {
            throw new ItemNotFoundException("Item not found with owner id " + ownerId);
        }

        if (item.getName() != null) {
            newItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }

        itemRepository.save(newItem);

        return toEntityItemDto(newItem);
    }

    @Override
    @Transactional
    public void delete(Long itemId) {
        itemRepository.deleteById(itemId);
    }


    @Override
    @Transactional
    public List<ItemDto> search(String text, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return toItemDtoList(itemRepository.search(text, pageRequest));
        }
    }

    @Override
    @Transactional
    public ItemDto getItemsByOwner(Long itemId, Long ownerId) {

        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(itemId);
        }
        Item item = itemRepository.findById(itemId).get();

        ItemDto itemDto = ItemMapper.toEntityItemDto(item);

        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException(ownerId);
        }

        if (item.getUser().getId().equals(ownerId)) {
            Optional<Booking> lastBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, LocalDateTime.now());
            if (lastBooking.isPresent()) {
                itemDto.setLastBooking(toBookingShortDto(lastBooking.get()));
            } else {
                itemDto.setLastBooking(null);
            }

            if (nextBooking.isPresent()) {
                itemDto.setNextBooking(toBookingShortDto(nextBooking.get()));
            } else {
                itemDto.setNextBooking(null);
            }
        }

        List<Comment> commentList = commentRepository.findAllByItemId(itemId);

        if (!commentList.isEmpty()) {
            itemDto.setComments(toCommentDtoList(commentList));
        } else {
            itemDto.setComments(Collections.emptyList());
        }

        return itemDto;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long ownerId, Long itemId, CommentDto commentDto) {
        User user = getUserById(ownerId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Object %s not found", Item.class)));
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), LocalDateTime.now())) {
            throw new IncorrectCommentException("User doesn't use this item");
        }
        Comment comment = commentRepository.save(CommentMapper.fromComment(commentDto, item, user, LocalDateTime.now()));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId, Integer from, Integer size) {
        checkUser(ownerId);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Item> items = itemRepository.findByUserIdOrderById(ownerId, pageRequest);
        List<ItemDto> itemDtos = ItemMapper.toItemDtoList(items);

        List<Booking> bookings = bookingRepository.findAllByItem_UserId(ownerId, Sort.by(Sort.Direction.ASC, "start"));
        List<BookingShortDto> bookingShortDtos = bookings.stream()
                .map(BookingMapper::toBookingShortDto)
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findAllByItemIdIn(
                items.stream()
                        .map(Item::getId)
                        .collect(Collectors.toList()),
                Sort.by(Sort.Direction.ASC, "created"));

        itemDtos.forEach(itemDto -> {
            setBookings(itemDto, bookingShortDtos);
            setComments(itemDto, comments);
        });

        return itemDtos;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private void setBookings(ItemDto itemDto, List<BookingShortDto> bookings) {
        itemDto.setLastBooking(bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemDto.getId()) &&
                        booking.getStart().isBefore(LocalDateTime.now()))
                .reduce((a, b) -> b).orElse(null));
        itemDto.setNextBooking(bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemDto.getId()) &&
                        booking.getStart().isAfter(LocalDateTime.now()))
                .reduce((a, b) -> a).orElse(null));
    }

    private void setComments(ItemDto itemDto, List<Comment> comments) {
        itemDto.setComments(comments.stream()
                .filter(comment -> comment.getItem().getId().equals(itemDto.getId()))
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
    }
}
