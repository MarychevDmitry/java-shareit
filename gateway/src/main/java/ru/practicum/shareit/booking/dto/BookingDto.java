package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookingDto {
    @NotNull(message = "Поле itemId обязательно")
    @Min(value = 1, message = "Некорректный itemId")
    private Long itemId;
    @NotNull(message = "Поле start обязательно")
    @FutureOrPresent(message = "Начало бронирования не может быть в прошлом")
    private LocalDateTime start;
    @NotNull(message = "Поле end обязательно")
    @Future(message = "Конец бронирования не может быть в прошлом")
    private LocalDateTime end;
    private Status status;
}
