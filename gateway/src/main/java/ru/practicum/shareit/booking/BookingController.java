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
import java.time.LocalDateTime;

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
		if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
			throw new ValidationException("Booking: Dates are null!");
		}
		if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isEqual(bookingDto.getEnd())
				|| bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
			throw new ValidationException("Booking: Problem in dates");
		}
		return bookingClient.addBooking(bookerId, bookingDto);
	}

	@PatchMapping("{bookingId}")
	public ResponseEntity<Object> confirmationBooking(@RequestHeader(Header.userIdHeader) @Min(1) Long ownerId,
													  @RequestParam String approved,
													  @PathVariable @Min(1) Long bookingId) {
		return bookingClient.confirmationBooking(ownerId, approved, bookingId);
	}

	@GetMapping("{bookingId}")
	public ResponseEntity<Object> getBookingById(
			@PathVariable @Min(1) Long bookingId,
			@RequestHeader(Header.userIdHeader) @Min(1) Long userId) {
		return bookingClient.getBookingById(bookingId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBrookingByBookerId(
			@RequestHeader(Header.userIdHeader) @Min(1) Long userId,
			@RequestParam(defaultValue = "ALL") String state,
			@RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
			@RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
		return bookingClient.getAllBrookingByBookerId(userId, state, from, size);
	}

	@GetMapping("owner")
	public ResponseEntity<Object> getAllBookingsForAllItemsByOwnerId(
			@RequestHeader(Header.userIdHeader) @Min(1) Long userId,
			@RequestParam(defaultValue = "ALL") String state,
			@RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
			@RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
		return bookingClient.getAllBookingsForAllItemsByOwnerId(userId, state, from, size);
	}
}
