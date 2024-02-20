package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findAllByItemUserIdOrderByStartDesc(Long userId);

    List<Booking> findAllByItemUserIdAndStartBeforeAndEndAfterOrderByStartAsc(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemUserIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByItemUserIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime dateTime);

    List<Booking> findAllByItemUserIdAndStatusOrderByStartDesc(Long userId, Status status);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(Long itemId, Status status, LocalDateTime dateTime);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndBefore(Long itemId, Long userId, Status status, LocalDateTime dateTime);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long id, Long id1, LocalDateTime now);
}
