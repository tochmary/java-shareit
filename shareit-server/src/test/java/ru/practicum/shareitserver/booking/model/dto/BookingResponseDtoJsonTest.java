package ru.practicum.shareitserver.booking.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareitserver.booking.Status;
import ru.practicum.shareitserver.item.model.dto.ItemDto;
import ru.practicum.shareitserver.user.model.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseDtoJsonTest {
    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    void testBookingResponseDto() throws Exception {
        UserDto booker = new UserDto(1L, "Maria", "maria_smart@mail.ru");
        ItemDto item = new ItemDto(1L, "Открытка", "сувенир4", true, null);

        LocalDateTime start = LocalDateTime.of(2022, 8, 29, 12, 12);
        LocalDateTime end = LocalDateTime.of(2022, 8, 30, 14, 15);

        BookingResponseDto bookingResponseDto = new BookingResponseDto(
                1L,
                start,
                end,
                item,
                booker,
                Status.WAITING);

        JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-08-29T12:12:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-08-30T14:15:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}