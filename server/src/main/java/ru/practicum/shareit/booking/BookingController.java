package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.utilitary.Constants.HEADER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public BookingOutDto addBookings(@RequestHeader(HEADER_USER_ID) Long userId, @RequestBody BookingDto bookingDto) {
        log.info("POST: request to the endpoint was received: '/bookings' user {}, add new booking {}", userId, "bookingDto.getName()");
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto confirmationBooking(@RequestHeader(HEADER_USER_ID) Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam(value = "approved") Boolean approved) {
        log.info("PATCH: request to the endpoint was received: '/bookings' user {}, changed the status booking {}", userId, bookingId);
        return bookingService.confirmationBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getBookingById(@RequestHeader(HEADER_USER_ID) Long userId,
                                        @PathVariable Long bookingId) {

        log.info("GET: request to the endpoint was received: '/bookings' get booking {}", bookingId);
        return bookingService.getBookingByIdAndBookerId(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDto> getAllBrookingByBookerId(@RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return bookingService.getAllBrookingByBookerId(PageRequest.of(from / size, size), userId, state);
    }

    @GetMapping("owner")
    public List<BookingOutDto> getAllBookingsForAllItemsByOwnerId(@RequestHeader(HEADER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return bookingService.getAllBookingsForAllItemsByOwnerId(PageRequest.of(from / size, size), userId, state);
    }
}