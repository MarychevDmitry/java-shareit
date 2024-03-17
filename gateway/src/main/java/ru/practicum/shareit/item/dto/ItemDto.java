package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
public class ItemDto {
    private Long id;
    @Pattern(regexp = "^[^ ].*[^ ]$", message = "Некорректное имя")
    @Size(max = 50)
    @NotNull(message = "Поле name обязательно")
    private String name;
    @Pattern(regexp = "^[^ ].*[^ ]$", message = "Некорректное описание")
    @Size(max = 200)
    @NotNull(message = "Поле description обязательно")
    private String description;
    @NotNull(message = "Поле available обязательно")
    private Boolean available;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;
    @Min(1)
    private Long requestId;
}
