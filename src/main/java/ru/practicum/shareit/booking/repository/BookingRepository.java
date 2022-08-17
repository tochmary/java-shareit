package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.entity.Booking;

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

    List<Booking> findAllByItemIdOrderByStartDesc(long itemId);

    List<Booking> findAllByItemIdAndStatusOrderByStartDesc(long itemId, Status status);

    List<Booking> findAllByItemIdAndEndBeforeOrderByStartDesc(long itemId, LocalDateTime endDate);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartDesc(long itemId, LocalDateTime startDate);

    List<Booking> findAllByItemIdAndStartBeforeAndEndAfterOrderByStartDesc(long itemId,
                                                                           LocalDateTime startDate,
                                                                           LocalDateTime endDate);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(long itemId, LocalDateTime endDate);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime startDate);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(long bookerId,
                                                      long itemId,
                                                      LocalDateTime endDate);
}
