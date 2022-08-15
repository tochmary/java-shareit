package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.exception.BadRequestException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId) {
        log.info("Получение данных о бронировании c bookingId={} для пользователя c userId={}", bookingId, userId);
        Booking booking = bookingService.getBookingById(userId, bookingId);
        return BookingMapper.toBookingDtoOut(booking);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получение списка бронирований для всех вещей пользователя c userId={} со статусом {}", userId, state);
        List<Booking> bookingList = bookingService.getBookingsByOwnerId(userId, toState(state));
        return BookingMapper.toBookingDtoList(bookingList);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получение списка всех бронирований пользователя c userId={} со статусом {}", userId, state);
        List<Booking> bookingList = bookingService.getBookingsByBookerId(userId, toState(state));
        return BookingMapper.toBookingDtoList(bookingList);
    }

    @PostMapping
    public BookingResponseDto addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Добавление нового бронирования {} пользователем userId={}", bookingRequestDto, userId);
        Booking booking = BookingMapper.toBooking(bookingRequestDto);
        booking = bookingService.addBooking(userId, bookingRequestDto.getItemId(), booking);
        return BookingMapper.toBookingDtoOut(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable long bookingId,
                                            @RequestParam Boolean approved) {
        log.info("Подтверждение или отклонение запроса на бронирование " +
                "с bookingId={} пользователем с userId {}. Подтверждение? {}", bookingId, userId, approved);
        Booking booking = bookingService.updateBookingStatus(userId, bookingId, approved);
        return BookingMapper.toBookingDtoOut(booking);
    }

    private State toState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException t) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }
}
