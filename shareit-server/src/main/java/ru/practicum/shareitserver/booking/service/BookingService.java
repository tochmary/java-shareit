package ru.practicum.shareitserver.booking.service;

import ru.practicum.shareitserver.booking.State;
import ru.practicum.shareitserver.booking.model.entity.Booking;
import ru.practicum.shareitserver.item.model.entity.Item;

import java.util.List;

public interface BookingService {
    Booking getBookingById(long userId, long bookingId);

    Booking addBooking(long userId, long itemId, Booking booking);

    Booking updateBookingStatus(long userId, long bookingId, Boolean approved);

    List<Booking> getBookingsByBookerId(long userId, State state, Integer from, Integer size);

    List<Booking> getBookingsByOwnerId(long userId, State state, Integer from, Integer size);

    Booking getLastBookingByItemId(long userId, Item item);

    Booking getNextBookingByItemId(long userId, Item item);
}
