package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.ItemValidationException;

public class BookingValidator {

    public static boolean isBookingValid(Booking booking) {
        if (booking.getStart() == null) {
            throw new BookingValidationException("Booking start date validation error.");
        } else if (booking.getStop() == null) {
            throw new ItemValidationException("Booking stop date validation error.");
        } else {
            return true;
        }
    }

    public static boolean isBookingDtoValid(BookingDto bookingDto) {
        if (bookingDto.getStart() == null) {
            throw new ItemValidationException("BookingDto start date validation error.");
        } else if (bookingDto.getEnd() == null) {
            throw new ItemValidationException("BookingDto end date validation error.");
        } else {
            return true;
        }
    }
}
