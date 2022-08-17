package ru.practicum.shareit.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * start — дата начала бронирования;
 * end — дата конца бронирования;
 * itemId — id вещи, которую пользователь бронирует;
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}

