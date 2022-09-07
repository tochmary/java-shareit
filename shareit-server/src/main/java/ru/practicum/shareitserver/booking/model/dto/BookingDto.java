package ru.practicum.shareitserver.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * id — уникальный идентификатор бронирования;
 * start — дата начала бронирования;
 * end — дата конца бронирования;
 * itemId — id вещи, которую пользователь бронирует;
 * bookerId — id пользователя, который осуществляет бронирование;
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
}
