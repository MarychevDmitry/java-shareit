package ru.practicum.shareit.booking;

import lombok.Data;

import java.sql.Date;

@Data
public class Booking {
    private Long id;
    private Date start;
    private Date stop;
    private Long itemId;
    private Long userId;
    private Boolean isConfirmed;
}
