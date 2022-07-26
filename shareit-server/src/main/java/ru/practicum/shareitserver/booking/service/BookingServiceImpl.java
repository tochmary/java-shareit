package ru.practicum.shareitserver.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.booking.BookingState;
import ru.practicum.shareitserver.booking.Status;
import ru.practicum.shareitserver.booking.model.entity.Booking;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.common.exception.BadRequestException;
import ru.practicum.shareitserver.common.exception.NotFoundException;
import ru.practicum.shareitserver.item.model.entity.Item;
import ru.practicum.shareitserver.item.service.ItemService;
import ru.practicum.shareitserver.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        log.debug("Просмотр бронирования c bookingId={} пользователем с userId={}", bookingId, userId);
        userService.checkUser(userId);
        Booking booking = getBookingById(bookingId);
        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();
        if (bookerId != userId && ownerId != userId) {
            throw new NotFoundException("Посмотреть бронирование может только автор бронирования или владелец вещи!");
        }
        log.debug("Получено бронирование {}", booking);
        return booking;
    }

    @Override
    @Transactional
    public Booking addBooking(long userId, long itemId, Booking booking) {
        log.debug("Добавление бронирования {} вещи с itemId={} пользователем с userId={}", booking, itemId, userId);
        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Нельзя забронировать вещь на прошлую дату!");
        }
        Item item = itemService.getItemId(userId, itemId);
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь с id " + itemId + " недоступна!");
        }
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Нельзя забронировать вещь владельцу!");
        }
        booking.setItem(item);
        booking.setBooker(userService.getUserById(userId));
        booking.setStatus(Status.WAITING);

        booking = bookingRepository.save(booking);
        log.debug("Добавлено бронирования {}", booking);
        return booking;
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(long userId, long bookingId, Boolean approved) {
        log.debug("Проставление статуса бронирования с bookingId={} пользователем с userId={}", bookingId, userId);
        Booking booking = getBookingById(bookingId);
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Подтвердить или отклонить запрос на бронирование может только владелец вещи!");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Подтвердить или отклонить запрос на бронирование может только в статусе WAITING!");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        booking = bookingRepository.save(booking);
        log.debug("Успешное проставление статуса для бронирования {}", booking);
        return booking;
    }

    @Override
    public List<Booking> getBookingsByBookerId(long userId, BookingState state, Integer from, Integer size) {
        log.debug("Получение бронирования для userId={} со state={}", userId, state);
        userService.checkUser(userId);
        log.info("from={}, size={}", from, size);
        PageRequest pr = PageRequest.of(from / size, size);
        List<Booking> bookingList;
        switch (state) {
            case WAITING:
            case REJECTED:
                Status status = Status.valueOf(String.valueOf(state));
                bookingList = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, status, pr).toList();
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pr).toList();
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pr).toList();
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pr).toList();
                break;
            default:
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pr).toList();
        }
        return bookingList;
    }

    @Override
    public List<Booking> getBookingsByOwnerId(long userId, BookingState state, Integer from, Integer size) {
        userService.checkUser(userId);
        log.debug("Получение бронирования вещей userId={} со state={}", userId, state);
        List<Item> itemList = itemService.getItemsByUserId(userId);
        log.info("from={}, size={}", from, size);
        PageRequest pr = PageRequest.of(from / size, size);
        return itemList.stream()
                .flatMap(item -> getBookingStream(state, item, pr))
                .collect(Collectors.toList());
    }

    @Override
    public Booking getLastBookingByItemId(long userId, Item item) {
        log.debug("Получение последнего бронирования вещи с itemId={} для владельца с userId={}", item.getId(), userId);
        userService.checkUser(userId);
        if (userId == item.getOwner().getId()) {
            return bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
        }
        return null;
    }

    @Override
    public Booking getNextBookingByItemId(long userId, Item item) {
        log.debug("Получение ближайшего бронирования вещи с itemId={} для владельца с userId={}", item.getId(), userId);
        userService.checkUser(userId);
        if (userId == item.getOwner().getId()) {
            return bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
        }
        return null;
    }

    private Booking getBookingById(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирования с bookingId=" + bookingId + " не существует!")
        );
    }

    private Stream<Booking> getBookingStream(BookingState state, Item item, PageRequest pr) {
        long itemId = item.getId();
        List<Booking> bookings;
        switch (state) {
            case WAITING:
            case REJECTED:
                Status status = Status.valueOf(String.valueOf(state));
                bookings = bookingRepository.findAllByItemIdAndStatusOrderByStartDesc(itemId, status, pr).toList();
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemIdAndEndBeforeOrderByStartDesc(itemId, LocalDateTime.now(), pr).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemIdAndStartBeforeAndEndAfterOrderByStartDesc(itemId, LocalDateTime.now(), LocalDateTime.now(), pr).toList();
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemIdAndStartAfterOrderByStartDesc(itemId, LocalDateTime.now(), pr).toList();
                break;
            default:
                bookings = bookingRepository.findAllByItemIdOrderByStartDesc(itemId, pr).toList();
        }
        return bookings.stream();
    }
}
