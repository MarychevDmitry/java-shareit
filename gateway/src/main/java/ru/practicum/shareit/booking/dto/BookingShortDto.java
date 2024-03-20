package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingShortDto {
    private Long id;
    private ItemDto item;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}