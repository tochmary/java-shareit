package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking getBookingById(long userId, long bookingId);

    Booking addBooking(long userId, long itemId, Booking booking);

    Booking updateBookingStatus(long userId, long bookingId, Boolean approved);

    List<Booking> getBookingsByBookerId(long userId, State state);

    List<Booking> getBookingsByOwnerId(long userId, State state);
}
