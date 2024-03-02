package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IncorrectDataException;
import ru.practicum.shareit.utilitary.Constants;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public BookingOutDto addBookings(@RequestHeader(Constants.HEADER_USER_ID) Long userId, @RequestBody @Valid BookingDto bookingDto) {
        log.info("POST: request to the endpoint was received: '/bookings' user {}, add new booking {}", userId, "bookingDto.getName()");
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new IncorrectDataException("Booking: Dates are null!");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isEqual(bookingDto.getEnd())
                || bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectDataException("Booking: Problem in dates");
        }
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto confirmationBooking(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam(value = "approved") Boolean approved) {
        log.info("PATCH: request to the endpoint was received: '/bookings' user {}, changed the status booking {}", userId, bookingId);
        return bookingService.confirmationBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getBookingById(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                        @PathVariable Long bookingId) {

        log.info("GET: request to the endpoint was received: '/bookings' get booking {}", bookingId);
        return bookingService.getBookingByIdAndBookerId(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDto> getAllBrookingByBookerId(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                        @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("GET: request to the endpoint was received: '/bookings' get all bookings by booker Id {}", userId);
        return bookingService.getAllBrookingByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllBookingsForAllItemsByOwnerId(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                                  @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET: request to the endpoint was received: '/bookings/owner' get all bookings for all items by owner Id {}", userId);
        return bookingService.getAllBookingsForAllItemsByOwnerId(userId, state);
    }
}
