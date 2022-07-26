package ru.practicum.shareitgateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.booking.dto.BookingRequestDto;
import ru.practicum.shareitgateway.booking.dto.BookingState;
import ru.practicum.shareitgateway.common.exception.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareitgateway.common.Constants.X_SHARER_USER_ID;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(X_SHARER_USER_ID) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                              @Positive @RequestParam(defaultValue = "20") Integer size) {
        BookingState state = getState(stateParam);
        log.info("Получение списка бронирований для state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size, false);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(X_SHARER_USER_ID) long userId,
                                           @RequestBody @Valid BookingRequestDto requestDto) {
        log.info("Добавление нового бронирования {} пользователем userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(X_SHARER_USER_ID) long userId,
                                             @PathVariable long bookingId) {
        log.info("Получение данных о бронировании c bookingId={}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerId(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                       @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получение списка бронирований для всех вещей пользователя c userId={} state {}, from={}, size={}",
                userId, stateParam, from, size);
        BookingState state = getState(stateParam);
        return bookingClient.getBookings(userId, state, from, size, true);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                @PathVariable long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("Подтверждение или отклонение запроса на бронирование " +
                "с bookingId={} пользователем с userId {}. Подтверждение? {}", bookingId, userId, approved);
        return bookingClient.updateBooking(bookingId, userId, approved);
    }

    private BookingState getState(String stateParam) {
        return BookingState.toState(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
    }
}
