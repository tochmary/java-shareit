package ru.practicum.shareitserver.booking;

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
    REJECTED
}
