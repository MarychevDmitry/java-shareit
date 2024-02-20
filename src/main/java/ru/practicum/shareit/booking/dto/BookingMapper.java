package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking fromBookingDto(BookingDto bookingDto) {

        Booking booking = Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();

        if (bookingDto.getStatus() == null) {
            booking.setStatus(Status.WAITING);
        } else {
            booking.setStatus(bookingDto.getStatus());
        }
        return booking;
    }

    public static BookingOutDto toBookingDto(Booking booking) {
        BookingOutDto bookingOutDto = BookingOutDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemMapper.toEntityItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .build();
        return bookingOutDto;
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static List<BookingOutDto> toBookingDtoList(Iterable<Booking> bookings) {
        List<BookingOutDto> result = new ArrayList<>();

        for (Booking booking : bookings) {
            result.add(toBookingDto(booking));
        }
        return result;
    }
}
