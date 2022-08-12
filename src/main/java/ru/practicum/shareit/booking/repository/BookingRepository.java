package ru.practicum.shareit.booking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime endDate);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime startDate);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId,
                                                                              LocalDateTime startDate,
                                                                              LocalDateTime endDate);


    List<Booking> findAllByItemIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByItemIdAndStatusOrderByStartDesc(long bookerId, Status status);

    List<Booking> findAllByItemIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime endDate);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime startDate);

    List<Booking> findAllByItemIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId,
                                                                             LocalDateTime startDate,
                                                                             LocalDateTime endDate);
}
