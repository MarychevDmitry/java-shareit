package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingShortDto {
    private Long id;
    private ItemDto item;
    private Long bookerId;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
}