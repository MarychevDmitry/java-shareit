package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
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

    @Transactional
    @Override
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

    @Transactional
    @Override
    public List<BookingOutDto> getAllBrookingByBookerId(Long userId, String state) {
        List<Booking> bookings = null;

        getUserById(userId);
        LocalDateTime localDate = LocalDateTime.now();
        State stateEnum = State.getEnumValue(state);

        switch (stateEnum) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, localDate, localDate);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, localDate);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, localDate);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                throw new BookingNotFoundException("Booking status not found");
        }
        return toBookingDtoList(bookings);
    }

    @Transactional
    @Override
    public List<BookingOutDto> getAllBookingsForAllItemsByOwnerId(Long userId, String state) {
        getUserById(userId);
        if (itemRepository.findByUserId(userId).isEmpty()) {
            throw new ValidationException("User does not have items to book");
        }
        List<Booking> bookings = null;
        LocalDateTime localDate = LocalDateTime.now();
        State stateEnum = State.getEnumValue(state);

        switch (stateEnum) {
            case ALL:
                bookings = bookingRepository.findAllByItemUserIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findAllByItemUserIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, localDate, localDate);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemUserIdAndEndBeforeOrderByStartDesc(userId, localDate);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemUserIdAndStartAfterOrderByStartDesc(userId, localDate);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            default:
                throw new BookingNotFoundException("Booking status not found");
        }
        return toBookingDtoList(bookings);
    }

    @Transactional
    @Override
    public BookingOutDto getBookingByIdAndBookerId(Long userId, Long bookingId) {
        getUserById(userId);
        Booking booking = getBookingById(bookingId);

        if (booking.getBooker().getId().equals(userId) || booking.getItem().getUser().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new BookingNotFoundException(bookingId);
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
