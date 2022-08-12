package ru.practicum.shareit.booking;

/**
 * ALL - все;
 * CURRENT - текущие;
 * PAST - завершённые;
 * FUTURE - будущие;
 * WAITING - ожидающие подтверждения;
 * REJECTED - отклонённые;
 **/
public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}