package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.State;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingDto;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingDtoList;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingOutDto addBooking(BookingDto bookingDto, Long userId) {
        User user = getUserById(userId);
        Item item = getItemById(bookingDto.getItemId());

        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        if (item.getUser().equals(user)) {
            throw new NotFoundException("Owner " + userId + " can't book his item");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Item " + item.getId() + " is booked");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Start cannot be later than end");
        }
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        return toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingOutDto confirmationBooking(Long userId, Long bookingId, Boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!Objects.equals(booking.getItem().getUser().getId(), userId)) {
            throw new NotFoundException("User with id = " + userId + " is not an owner!");
        }

        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new IncorrectDataException("Status is Approved");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return toBookingDto(booking);
    }

    @Override
    @Transactional
    public List<BookingOutDto> getAllBrookingByBookerId(Pageable pageable, Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        } else {
            return getListBookings(pageable, state, userId, false);
        }
    }

    @Override
    @Transactional
    public List<BookingOutDto> getAllBookingsForAllItemsByOwnerId(Pageable pageable, Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new ItemNotFoundException(String.format("User with id=%s not exist", userId));
        }
        if (!itemRepository.existsItemByUserId(userId)) {
            throw new UserNotFoundException(userId);
        } else {
            return getListBookings(pageable, state, userId, true);
        }
    }

    @Override
    @Transactional
    public BookingOutDto getBookingByIdAndBookerId(Long userId, Long bookingId) {
        getUserById(userId);
        Booking booking = getBookingById(bookingId);

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getUser().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new BookingNotFoundException(bookingId);
        }
    }

    private List<BookingOutDto> getListBookings(Pageable pageable, String state, Long userId, Boolean isOwner) {
        List<Long> itemsId = itemRepository.findAllItemIdByOwnerId(userId);
        switch (State.getEnumValue(state.toUpperCase())) {
            case ALL:
                if (isOwner) {
                    return toBookingDtoList( bookingRepository.findAllByItemIdInOrderByStartDesc(pageable, itemsId));
                } else {
                    return toBookingDtoList( bookingRepository.findAllByBookerIdOrderByStartDesc(pageable, userId));
                }
            case CURRENT:
                if (isOwner) {
                    return toBookingDtoList( bookingRepository.findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                            pageable, itemsId, LocalDateTime.now(), LocalDateTime.now()));
                } else {
                    return toBookingDtoList( bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                            pageable, userId, LocalDateTime.now(), LocalDateTime.now()));
                }
            case PAST:
                if (isOwner) {
                    return toBookingDtoList( bookingRepository
                                    .findAllByItemIdInAndEndIsBeforeOrderByStartDesc(
                                            pageable, itemsId, LocalDateTime.now()));
                } else {
                    return toBookingDtoList( bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(
                                            pageable, userId, LocalDateTime.now()));
                }
            case FUTURE:
                if (isOwner) {
                    return toBookingDtoList( bookingRepository
                                    .findAllByItemIdInAndStartIsAfterOrderByStartDesc(pageable, itemsId, LocalDateTime.now()));
                } else {
                    return toBookingDtoList( bookingRepository
                                    .findAllByBookerIdAndStartIsAfterOrderByStartDesc(pageable, userId, LocalDateTime.now()));
                }
            case WAITING:
                if (isOwner) {
                    return toBookingDtoList( bookingRepository
                                    .findAllByItemIdInAndStatusIsOrderByStartDesc(pageable, itemsId, Status.WAITING));
                } else {
                    return toBookingDtoList( bookingRepository
                                    .findAllByBookerIdAndStatusIsOrderByStartDesc(pageable, userId, Status.WAITING));
                }
            case REJECTED:
                if (isOwner) {
                    return toBookingDtoList( bookingRepository
                                    .findAllByItemIdInAndStatusIsOrderByStartDesc(pageable, itemsId, Status.REJECTED));
                } else {
                    return toBookingDtoList(bookingRepository
                                    .findAllByBookerIdAndStatusIsOrderByStartDesc(pageable, userId, Status.REJECTED));
                }
            default:
                throw new IncorrectStatusException("Unknown state: " + state);
        }
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
    }
}
