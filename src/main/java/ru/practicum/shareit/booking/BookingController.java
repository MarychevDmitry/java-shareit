package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utilitary.Constants;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.utilitary.Constants.HEADER_USER_ID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public BookingOutDto addBookings(@RequestHeader(Constants.HEADER_USER_ID) @Min(1) Long userId, @RequestBody @Valid BookingDto bookingDto) {
        log.info("POST: request to the endpoint was received: '/bookings' user {}, add new booking {}", userId, "bookingDto.getName()");
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Booking: Dates are null!");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isEqual(bookingDto.getEnd())
                || bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Booking: Problem in dates");
        }
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto confirmationBooking(@RequestHeader(Constants.HEADER_USER_ID) @Min(1) Long userId,
                                             @PathVariable @Min(1) Long bookingId,
                                             @RequestParam(value = "approved") Boolean approved) {
        log.info("PATCH: request to the endpoint was received: '/bookings' user {}, changed the status booking {}", userId, bookingId);
        return bookingService.confirmationBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getBookingById(@RequestHeader(Constants.HEADER_USER_ID) @Min(1) Long userId,
                                        @PathVariable @Min(1) Long bookingId) {

        log.info("GET: request to the endpoint was received: '/bookings' get booking {}", bookingId);
        return bookingService.getBookingByIdAndBookerId(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDto> getAllBrookingByBookerId(@RequestHeader(HEADER_USER_ID) @Min(1) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return bookingService.getAllBrookingByBookerId(PageRequest.of(from / size, size), userId, state);
    }

    @GetMapping("owner")
    public List<BookingOutDto> getAllBookingsForAllItemsByOwnerId(@RequestHeader(HEADER_USER_ID) @Min(1) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return bookingService.getAllBookingsForAllItemsByOwnerId(PageRequest.of(from / size, size), userId, state);
    }
}