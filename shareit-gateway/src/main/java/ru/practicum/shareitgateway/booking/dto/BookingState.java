package ru.practicum.shareitgateway.booking.dto;

import java.util.Optional;

/**
 * ALL - все;
 * CURRENT - текущие;
 * PAST - завершённые;
 * FUTURE - будущие;
 * WAITING - ожидающие подтверждения;
 * REJECTED - отклонённые;
 **/
public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> toState(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
