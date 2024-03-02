package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class ItemRequestDto {
    private Integer id;
    private String description;
    private String requesterName;
    private Date created;
}
