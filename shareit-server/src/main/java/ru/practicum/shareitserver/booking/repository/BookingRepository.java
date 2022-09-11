package ru.practicum.shareitserver.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareitserver.booking.Status;
import ru.practicum.shareitserver.booking.model.entity.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime endDate, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime startDate, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId,
                                                                             LocalDateTime startDate,
                                                                             LocalDateTime endDate,
                                                                             Pageable pageable);

    Page<Booking> findAllByItemIdOrderByStartDesc(long itemId, Pageable pageable);

    Page<Booking> findAllByItemIdAndStatusOrderByStartDesc(long itemId, Status status, Pageable pageable);

    Page<Booking> findAllByItemIdAndEndBeforeOrderByStartDesc(long itemId, LocalDateTime endDate, Pageable pageable);

    Page<Booking> findAllByItemIdAndStartAfterOrderByStartDesc(long itemId, LocalDateTime startDate, Pageable pageable);

    Page<Booking> findAllByItemIdAndStartBeforeAndEndAfterOrderByStartDesc(long itemId,
                                                                           LocalDateTime startDate,
                                                                           LocalDateTime endDate,
                                                                           Pageable pageable);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(long itemId, LocalDateTime endDate);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime startDate);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(long bookerId,
                                                      long itemId,
                                                      LocalDateTime endDate);
}
