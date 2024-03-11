package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;


public interface BookingService {
    BookingOutDto addBooking(BookingDto bookingDto, Long userId);

    BookingOutDto confirmationBooking(Long userId, Long bookingId, Boolean approved);

    List<BookingOutDto> getAllBrookingByBookerId(Long userId, String state);

    List<BookingOutDto> getAllBookingsForAllItemsByOwnerId(Long userId, String state);

    BookingOutDto getBookingByIdAndBookerId(Long userId, Long bookingId);
}
