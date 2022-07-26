package ru.practicum.shareitserver.booking;

/**
 * WAITING — новое бронирование, ожидает одобрения,
 * APPROVED — бронирование подтверждено владельцем,
 * REJECTED — бронирование отклонено владельцем,
 * CANCELED — бронирование отменено создателем.
 */
public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}
