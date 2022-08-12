package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.BadRequestException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public Booking getBookingById(long userId, long bookingId) {
        userService.checkUser(userId);
        Booking booking = getBookingById(bookingId);
        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwnerId();
        //BadRequestException
        if (bookerId != userId && ownerId != userId) {
            throw new NotFoundException("Посмотреть бронирование может только автор бронирования или владелец вещи!");
        }
        log.debug("Получено бронирование {}", booking);
        return booking;
    }


    @Override
    @Transactional
    public Booking addBooking(long userId, long itemId, Booking booking) {
        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Нельзя забронировать вещь на прошлую дату!");
        }
        Item item = itemService.getItemId(userId, itemId);
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь с id " + itemId + " недоступна!");
        }if (item.getOwnerId()==userId) {
            throw new NotFoundException("Нельзя забронировать вещь владельцу!");
        }
        booking.setItem(item);
        booking.setBooker(userService.getUserById(userId));
        booking.setStatus(Status.WAITING);
        log.debug("Добавление бронирования {}", booking);
        booking = bookingRepository.save(booking);

        log.debug("Добавлено бронирования {}", booking);
        return booking;
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(long userId, long bookingId, Boolean approved) {
        log.debug("updateBookingStatus");
        Booking booking = getBookingById(bookingId);
        //BadRequestException
        if (!Objects.equals(booking.getItem().getOwnerId(), userId)) {
            throw new NotFoundException("Подтвердить или отклонить запрос на бронирование может только владелец вещи!");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Подтвердить или отклонить запрос на бронирование может только в статусе WAITING!");
        }
        Status status = approved ? Status.APPROVED : Status.REJECTED;
        booking.setStatus(status);
        log.debug("Проставление статуса бронирования {}", status);
        booking = bookingRepository.save(booking);
        log.debug("Успешное проставление статуса для бронирования {}", booking);
        return booking;
    }

    @Override
    public List<Booking> getBookingsByBookerId(long userId, State state) {
        userService.checkUser(userId);
        log.debug("Получения бронирования для userId {} со state {}", userId, state);
        List<Booking> bookingList;
        switch (state) {
            case WAITING:
            case REJECTED:
                Status status = Status.valueOf(String.valueOf(state));
                bookingList = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, status);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            default:
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        }
        return bookingList;
    }

    @Override
    public List<Booking> getBookingsByOwnerId(long userId, State state) {
        userService.checkUser(userId);
        log.debug("Получения бронирования вещей userId {} со state {}", userId, state);
        List<Item> itemList = itemService.getItemsByUserId(userId);
        List<Booking> bookingList = itemList.stream()
                .flatMap(item -> {
                    long itemId = item.getId();
                    List<Booking> bookings;
                    switch (state) {
                        case WAITING:
                        case REJECTED:
                            Status status = Status.valueOf(String.valueOf(state));
                            bookings = bookingRepository.findAllByItemIdAndStatusOrderByStartDesc(itemId, status);
                            break;
                        case PAST:
                            bookings = bookingRepository.findAllByItemIdAndEndBeforeOrderByStartDesc(itemId, LocalDateTime.now());
                            break;
                        case CURRENT:
                            bookings = bookingRepository.findAllByItemIdAndStartBeforeAndEndAfterOrderByStartDesc(itemId, LocalDateTime.now(), LocalDateTime.now());
                            break;
                        case FUTURE:
                            bookings = bookingRepository.findAllByItemIdAndStartAfterOrderByStartDesc(itemId, LocalDateTime.now());
                            break;
                        default:
                            bookings = bookingRepository.findAllByItemIdOrderByStartDesc(itemId);
                    }
                    return bookings.stream();
                })
                .collect(Collectors.toList());
        return bookingList;
    }

    private Booking getBookingById(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирования с id = " + bookingId + " не существует!")
        );
    }
}
