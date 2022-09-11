package ru.practicum.shareitserver.booking.mapper;

import ru.practicum.shareitserver.booking.model.dto.BookingDto;
import ru.practicum.shareitserver.booking.model.dto.BookingRequestDto;
import ru.practicum.shareitserver.booking.model.dto.BookingResponseDto;
import ru.practicum.shareitserver.booking.model.entity.Booking;
import ru.practicum.shareitserver.item.mapper.ItemMapper;
import ru.practicum.shareitserver.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static BookingResponseDto toBookingDtoOut(Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setStart(booking.getStart());
        bookingResponseDto.setEnd(booking.getEnd());
        bookingResponseDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingResponseDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingResponseDto.setStatus(booking.getStatus());
        return bookingResponseDto;
    }

    public static List<BookingResponseDto> toBookingDtoList(List<Booking> bookingList) {
        return bookingList.stream()
                .map(BookingMapper::toBookingDtoOut)
                .collect(Collectors.toList());
    }

    public static Booking toBooking(BookingRequestDto bookingRequestDto) {
        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setBookerId(booking.getBooker().getId());
        return bookingDto;
    }
}
