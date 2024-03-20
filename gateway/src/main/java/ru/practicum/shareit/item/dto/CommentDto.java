package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    public Long id;
    @NotBlank(message = "поле text не должно быть пустым")
    @Size(max = 200, message = "Превышена максимальная длина сообщения")
    private String text;
    private LocalDateTime created;
    private String authorName;
}