package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.entity.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.CommentValidationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingShortDto;
import static ru.practicum.shareit.comment.dto.CommentMapper.toCommentDtoList;
import static ru.practicum.shareit.item.ItemValidator.isItemValid;
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
    public ItemDto getById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        return ItemMapper.toEntityItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        checkUser(userId);

        User user = userRepository.findById(userId).get();
        Item item = fromEntityItemDto(itemDto, user);
        if (itemDto.getRequestId() != null) {
            if (!itemRequestRepository.existsById(itemDto.getRequestId())) {
                throw new RequestNotFoundException(itemDto.getRequestId());
            }
            item.setRequest(itemRequestRepository.findById(itemDto.getRequestId()).get());
        }
        itemRepository.save(item);

        return toEntityItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        checkUser(ownerId);

        User user = userRepository.findById(ownerId).get();

        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException(itemId);
        }
        Item item = fromEntityItemDto(itemDto, user);

        item.setId(itemId);

        if (!itemRepository.findByUserId(ownerId).contains(item)) {
            throw new ItemNotFoundException("Item not found with owner id " + ownerId);
        }

        Item newItem = itemRepository.findById(item.getId()).get();

        if (item.getName() != null) {
            newItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }

        isItemValid(newItem);

        itemRepository.save(newItem);

        return toEntityItemDto(newItem);
    }

    @Override
    @Transactional
    public void delete(Long itemId) {
        itemRepository.delete(getItemById(itemId));
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

    @Transactional
    @Override
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

            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, LocalDateTime.now());

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

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long ownerId, Long itemId, CommentDto commentDto) {
        User user = getUserById(ownerId);
        if (commentDto.getText().isEmpty())
            throw new CommentValidationException("Comment text can't be empty");
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Объект класса %s не найден", Item.class)));
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), LocalDateTime.now())) {
            throw new CommentValidationException("User doesn't use this item");
        }
        Comment comment = commentRepository.save(CommentMapper.fromComment(commentDto, item, user, LocalDateTime.now()));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId, Integer from, Integer size) {
        checkUser(ownerId);

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<ItemDto> resultList = new ArrayList<>();

        for (ItemDto itemDto : ItemMapper.toItemDtoList(itemRepository.findByUserIdOrderById(ownerId, pageRequest))) {

            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemDto.getId(), Status.APPROVED, LocalDateTime.now());

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

            resultList.add(itemDto);
        }

        for (ItemDto itemDto : resultList) {

            List<Comment> commentList = commentRepository.findAllByItemId(itemDto.getId());

            if (!commentList.isEmpty()) {
                itemDto.setComments(toCommentDtoList(commentList));
            } else {
                itemDto.setComments(Collections.emptyList());
            }
        }

        return resultList;
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
