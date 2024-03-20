package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItem_UserId(long ownerId, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(Long itemId, Status status, LocalDateTime dateTime);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status, LocalDateTime now);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long id, Long id1, LocalDateTime now);

    List<Booking> findAllByItemIdInOrderByStartDesc(Pageable pageable, Collection<Long> itemId);

    List<Booking> findAllByBookerIdOrderByStartDesc(Pageable pageable, Long bookerId);

    List<Booking> findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Pageable pageable, Collection<Long> itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Pageable pageable, Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemIdInAndEndIsBeforeOrderByStartDesc(
            Pageable pageable, Collection<Long> itemId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Pageable pageable, Long bookerId, LocalDateTime end);

    List<Booking> findAllByItemIdInAndStartIsAfterOrderByStartDesc(
            Pageable pageable, Collection<Long> itemId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(
            Pageable pageable, Long bookerId, LocalDateTime start);

    List<Booking> findAllByItemIdInAndStatusIsOrderByStartDesc(
            Pageable pageable, Collection<Long> itemId, Status status);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(Pageable pageable, Long bookerId, Status status);
}
