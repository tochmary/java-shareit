package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.user.model.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final User user1 = new User(1L, "Maria", "maria_smart@mail.ru");
    private final User user2 = new User(2L, "Ivan", "ivan_humble@mail.ru");

    private final Item item1 = new Item(1L, "Магнит", "сувенир", true, user1, null);
    private final Item item2 = new Item(2L, "Открытка", "сувенир", true, user1, null);

    private final Booking booking1 = new Booking(1L,
            LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item1, user2, Status.WAITING);
    private final Booking booking2 = new Booking(2L,
            LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), item2, user2, Status.WAITING);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    @DisplayName("Получение данных о бронировании")
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(booking1);

        mvc.perform(get("/bookings/{bookingId}", booking1.getId())
                        .header("X-Sharer-User-Id", booking1.getBooker().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking1.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(booking1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(booking1.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking1.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking1.getStatus().toString())));
    }

    @Test
    @DisplayName("Получение списка бронирований для всех вещей пользователя")
    void getBookingsByOwnerId() throws Exception {
        List<Booking> bookings = List.of(booking1, booking2);
        when(bookingService.getBookingsByOwnerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        List<BookingResponseDto> bookingResponseDtoList = BookingMapper.toBookingDtoList(bookings);
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", booking1.getItem().getOwner().getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingResponseDtoList)));
    }

    @Test
    @DisplayName("Получение списка всех бронирований пользователя")
    void getBookingsByBookerId() throws Exception {
        List<Booking> bookings = List.of(booking1, booking2);
        when(bookingService.getBookingsByBookerId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        List<BookingResponseDto> bookingResponseDtoList = BookingMapper.toBookingDtoList(bookings);
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", booking1.getBooker().getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingResponseDtoList)));
    }

    @Test
    @DisplayName("Добавление нового бронирования пользователем")
    void addBooking() throws Exception {
        when(bookingService.addBooking(anyLong(), anyLong(), any()))
                .thenReturn(booking1);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booking1.getBooker().getId())
                        .content(mapper.writeValueAsString(BookingMapper.toBookingDto(booking1)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking1.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(booking1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(booking1.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking1.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking1.getStatus().toString())));
    }

    @Test
    @DisplayName("Подтверждение запроса на бронирование пользователем")
    void updateBooking() throws Exception {
        Booking newBooking = copyBooking(booking1);
        newBooking.setStatus(Status.APPROVED);
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(newBooking);

        mvc.perform(patch("/bookings/{bookingId}", booking1.getId())
                        .header("X-Sharer-User-Id", booking1.getBooker().getId())
                        .param("approved", String.valueOf(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(booking1.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(booking1.getEnd().format(formatter))))
                .andExpect(jsonPath("$.item.id", is(booking1.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking1.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())));
    }

    private Booking copyBooking(Booking booking) {
        Booking newBooking = new Booking();
        newBooking.setId(booking.getId());
        newBooking.setStart(booking.getStart());
        newBooking.setEnd(booking.getEnd());
        newBooking.setItem(booking.getItem());
        newBooking.setBooker(booking.getBooker());
        newBooking.setStatus(booking.getStatus());
        return newBooking;
    }
}