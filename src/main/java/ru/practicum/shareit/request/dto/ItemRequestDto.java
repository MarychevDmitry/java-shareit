package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.entity.User;

import java.sql.Date;

@Data
@Builder
public class ItemRequestDto {
    private Integer id;
    private String description;
    private User requester;
    private Date created;
}
