package ru.practicum.shareitserver.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareitserver.booking.BookingState;
import ru.practicum.shareitserver.booking.Status;
import ru.practicum.shareitserver.booking.model.entity.Booking;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.item.model.entity.Item;
import ru.practicum.shareitserver.item.service.ItemService;
import ru.practicum.shareitserver.user.model.entity.User;
import ru.practicum.shareitserver.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    BookingRepository mockBookingRepository;
    @Mock
    ItemService mockItemService;
    @Mock
    UserService mockUserService;

    BookingServiceImpl bookingServiceImpl;

    private static final User USER_1 = new User(1L, "Maria", "maria_smart@mail.ru");
    private static final User USER_2 = new User(2L, "Ivan", "ivan_humble@mail.ru");

    private static final Item ITEM_1 = new Item(1L, "Магнит", "сувенир", true, USER_1, null);
    private static final Item ITEM_2 = new Item(2L, "Открытка", "сувенир", true, USER_1, null);

    private static final Booking BOOKING_1 = new Booking(1L,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), ITEM_1, USER_2, Status.WAITING);
    private static final Booking BOOKING_2 = new Booking(2L,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), ITEM_2, USER_2, Status.WAITING);

    @BeforeEach
    void setUp() {
        bookingServiceImpl = new BookingServiceImpl(mockBookingRepository, mockItemService, mockUserService);
    }

    @Test
    @DisplayName("Просмотр бронирования")
    void getBookingById() {
        Mockito
                .when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(BOOKING_1));

        Booking targetBooking = bookingServiceImpl.getBookingById(USER_1.getId(), BOOKING_1.getId());
        checkFields(targetBooking, BOOKING_1);
    }

    @Test
    @DisplayName("Добавление бронирования")
    void addBooking() {
        Mockito
                .when(mockItemService.getItemId(anyLong(), anyLong()))
                .thenReturn(ITEM_1);
        Mockito
                .when(mockUserService.getUserById(anyLong()))
                .thenReturn(USER_2);
        Mockito
                .when(mockBookingRepository.save(any()))
                .thenReturn(BOOKING_1);

        Booking targetBooking = bookingServiceImpl.addBooking(USER_2.getId(), ITEM_1.getId(), BOOKING_1);
        checkFields(targetBooking, BOOKING_1);
    }

    @Test
    @DisplayName("Проставление статуса бронирования")
    void updateBookingStatus() {
        Mockito
                .when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(BOOKING_1));
        Booking sourceBooking = copyBooking(BOOKING_1);
        sourceBooking.setStatus(Status.APPROVED);
        Mockito
                .when(mockBookingRepository.save(any()))
                .thenReturn(sourceBooking);

        Booking targetBooking = bookingServiceImpl.updateBookingStatus(USER_1.getId(), BOOKING_1.getId(), true);
        checkFields(targetBooking, sourceBooking);
    }

    @Test
    @DisplayName("Получение бронирования для пользователя со state WAITING")
    void getBookingsByBookerId() {
        List<Booking> sourceBookings = List.of(BOOKING_1, BOOKING_2);
        Mockito
                .when(mockBookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(sourceBookings));

        List<Booking> targetBookings = bookingServiceImpl.getBookingsByBookerId(USER_1.getId(), BookingState.WAITING, 0, 20);
        Assertions.assertEquals(sourceBookings.size(), targetBookings.size());
    }

    @Test
    @DisplayName("Получение бронирования вещей пользователя со state WAITING")
    void getBookingsByOwnerId() {
        List<Booking> sourceBookings = List.of(BOOKING_1, BOOKING_2);
        Mockito
                .when(mockItemService.getItemsByUserId(anyLong()))
                .thenReturn(List.of(ITEM_1, ITEM_2));
        Mockito
                .when(mockBookingRepository.findAllByItemIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenAnswer(invocationOnMock -> {
                    long itemId = invocationOnMock.getArgument(0, Long.class);
                    if (itemId == 1) {
                        return new PageImpl<>(List.of(BOOKING_1));
                    } else if (itemId == 2) {
                        return new PageImpl<>(List.of(BOOKING_2));
                    }
                    return null;
                });

        List<Booking> targetBookings = bookingServiceImpl.getBookingsByOwnerId(USER_1.getId(), BookingState.WAITING, 0, 20);
        Assertions.assertEquals(sourceBookings.size(), targetBookings.size());
    }

    @Test
    @DisplayName("Получение последнего бронирования вещи")
    void getLastBookingByItemId() {
        Mockito
                .when(mockBookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(anyLong(), any()))
                .thenReturn(BOOKING_1);

        Booking targetBooking = bookingServiceImpl.getLastBookingByItemId(USER_1.getId(), ITEM_1);
        checkFields(targetBooking, BOOKING_1);
    }

    @Test
    @DisplayName("Получение ближайшего бронирования вещи")
    void getNextBookingByItemId() {
        Mockito
                .when(mockBookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(anyLong(), any()))
                .thenReturn(BOOKING_1);

        Booking targetBooking = bookingServiceImpl.getNextBookingByItemId(USER_1.getId(), ITEM_1);
        checkFields(targetBooking, BOOKING_1);
    }

    private void checkFields(Booking targetBooking, Booking sourceBooking) {
        assertThat(targetBooking.getId(), equalTo(sourceBooking.getId()));
        assertThat(targetBooking.getStart(), equalTo(sourceBooking.getStart()));
        assertThat(targetBooking.getEnd(), equalTo(sourceBooking.getEnd()));
        assertThat(targetBooking.getItem(), equalTo(sourceBooking.getItem()));
        assertThat(targetBooking.getBooker(), equalTo(sourceBooking.getBooker()));
        assertThat(targetBooking.getStatus(), equalTo(sourceBooking.getStatus()));
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