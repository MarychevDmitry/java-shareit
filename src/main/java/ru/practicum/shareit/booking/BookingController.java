package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utilitary.Constants;

import java.util.List;

import static ru.practicum.shareit.booking.BookingValidator.isBookingDtoValid;


@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    private static final String HEADER_USER_ID = Constants.HEADER_USER_ID;

    @PostMapping()
    public BookingOutDto addBookings(@RequestHeader(HEADER_USER_ID) Long userId, @RequestBody BookingDto bookingDto) {
        isBookingDtoValid(bookingDto);
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
        return bookingService.getBookingById(userId, bookingId);
    }


    @GetMapping
    public List<BookingOutDto> getAllBrookingByBookerId(@RequestHeader(HEADER_USER_ID) Long userId,
                                                        @RequestParam(value = "state", defaultValue = "ALL") String state) {
        log.info("GET: request to the endpoint was received: '/bookings' get all bookings by booker Id {}", userId);
        return bookingService.getAllBrookingByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllBookingsForAllItemsByOwnerId(@RequestHeader(HEADER_USER_ID) Long userId,
                                                                  @RequestParam(defaultValue = "ALL") String state) {

        log.info("GET: request to the endpoint was received: '/bookings/owner' get all bookings for all items by owner Id {}", userId);
        return bookingService.getAllBookingsForAllItemsByOwnerId(userId, state);
    }

}
