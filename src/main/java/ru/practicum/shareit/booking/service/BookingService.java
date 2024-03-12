package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

public interface BookingService {
    BookingOutDto addBooking(BookingDto bookingDto, Long userId);

    BookingOutDto confirmationBooking(Long userId, Long bookingId, Boolean approved);

    BookingOutDto getBookingByIdAndBookerId(Long userId, Long bookingId);

    List<BookingOutDto> getAllBrookingByBookerId(Pageable pageable, Long userId, String state);

    List<BookingOutDto> getAllBookingsForAllItemsByOwnerId(Pageable pageable, Long userId, String state);
}
