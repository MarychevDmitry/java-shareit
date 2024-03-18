package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.common.Header;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader(Header.userIdHeader) @Min(1) Long bookerId,
												@Valid @RequestBody BookingDto bookingDto) {
		if(!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
			throw new ValidationException("Booking: Problem in dates");
		}
		log.info("POST: request to the endpoint was received: '/bookings' user {}, add new booking {}", bookerId, "bookingDto.getName()");
		return bookingClient.addBooking(bookerId, bookingDto);
	}

	@PatchMapping("{bookingId}")
	public ResponseEntity<Object> confirmationBooking(@RequestHeader(Header.userIdHeader) @Min(1) Long ownerId,
													  @RequestParam String approved,
													  @PathVariable @Min(1) Long bookingId) {
		log.info("PATCH: request to the endpoint was received: '/bookings' user {}, changed the status booking {}", ownerId, bookingId);
		return bookingClient.confirmationBooking(ownerId, approved, bookingId);
	}

	@GetMapping("{bookingId}")
	public ResponseEntity<Object> getBookingById(
			@PathVariable @Min(1) Long bookingId,
			@RequestHeader(Header.userIdHeader) @Min(1) Long userId) {
		log.info("GET: request to the endpoint was received: '/bookings' get booking {}", bookingId);
		return bookingClient.getBookingById(bookingId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBrookingByBookerId(
			@RequestHeader(Header.userIdHeader) @Min(1) Long userId,
			@RequestParam(defaultValue = "ALL") String state,
			@RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
			@RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
		log.info("GET: request to the endpoint was received: '/bookings?state={state}&&from={from}&&size={size}");
		return bookingClient.getAllBrookingByBookerId(userId, state, from, size);
	}

	@GetMapping("owner")
	public ResponseEntity<Object> getAllBookingsForAllItemsByOwnerId(
			@RequestHeader(Header.userIdHeader) @Min(1) Long userId,
			@RequestParam(defaultValue = "ALL") String state,
			@RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
			@RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
		log.info("GET: request to the endpoint was received: '/bookings/owner?state={state}&&from={from}&&size={size}");
		return bookingClient.getAllBookingsForAllItemsByOwnerId(userId, state, from, size);
	}
}
