package ru.practicum.shareitserver.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareitserver.booking.service.BookingService;
import ru.practicum.shareitserver.item.mapper.CommentMapper;
import ru.practicum.shareitserver.item.mapper.ItemMapper;
import ru.practicum.shareitserver.item.model.dto.ItemDto;
import ru.practicum.shareitserver.item.model.entity.Comment;
import ru.practicum.shareitserver.item.model.entity.Item;
import ru.practicum.shareitserver.item.service.ItemService;
import ru.practicum.shareitserver.user.model.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareitserver.common.Constants.X_SHARER_USER_ID;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final User user1 = new User(1L, "Maria", "maria_smart@mail.ru");
    private final User user2 = new User(2L, "Ivan", "ivan_humble@mail.ru");

    private final Item item1 = new Item(1L, "Магнит", "сувенир1", true, user1, null);
    private final Item item2 = new Item(2L, "Открытка", "сувенир2", true, user1, null);

    private final Comment comment1 = new Comment(1L, "комментарий1", item1, user2, LocalDateTime.now());

    @Test
    @DisplayName("Получение списка вещей владельца")
    void getItems() throws Exception {
        List<Item> items = List.of(item1, item2);
        when(itemService.getItemsByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(items);
        when(itemService.getCommentsByItemId(anyLong()))
                .thenReturn(null);
        when(bookingService.getLastBookingByItemId(anyLong(), any()))
                .thenReturn(null);
        when(bookingService.getNextBookingByItemId(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item1.getName())))
                .andExpect(jsonPath("$[0].description", is(item1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item1.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", is(nullValue())))
                .andExpect(jsonPath("$[0].nextBooking", is(nullValue())))
                .andExpect(jsonPath("$[0].comments", is(nullValue())))
                .andExpect(jsonPath("$[1].id", is(item2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(item2.getName())))
                .andExpect(jsonPath("$[1].description", is(item2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(item2.getAvailable())))
                .andExpect(jsonPath("$[1].lastBooking", is(nullValue())))
                .andExpect(jsonPath("$[1].nextBooking", is(nullValue())))
                .andExpect(jsonPath("$[1].comments", is(nullValue())));
    }

    @Test
    @DisplayName("Просмотр вещи пользователем")
    void getItemById() throws Exception {
        when(itemService.getItemId(anyLong(), anyLong()))
                .thenReturn(item1);

        List<Comment> comments = List.of(comment1);
        when(itemService.getCommentsByItemId(anyLong()))
                .thenReturn(comments);
        when(bookingService.getLastBookingByItemId(anyLong(), any()))
                .thenReturn(null);
        when(bookingService.getNextBookingByItemId(anyLong(), any()))
                .thenReturn(null);

        mvc.perform(get("/items/{itemId}", user1.getId())
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.available", is(item1.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", is(nullValue())))
                .andExpect(jsonPath("$.nextBooking", is(nullValue())))
                .andExpect(jsonPath("$.comments[0].id", is(comment1.getId()), Long.class));
    }

    @Test
    @DisplayName("Добавление вещи пользователем")
    void addItem() throws Exception {
        when(itemService.addItem(anyLong(), any(), any()))
                .thenReturn(item1);

        mvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, item1.getOwner().getId())
                        .content(mapper.writeValueAsString(ItemMapper.toItemDto(item1)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.available", is(item1.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(nullValue())));
    }

    @Test
    @DisplayName("Обновление вещи пользователем")
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(), any()))
                .thenReturn(item1);

        mvc.perform(patch("/items/{itemId}", item1.getId())
                        .header(X_SHARER_USER_ID, item1.getOwner().getId())
                        .content(mapper.writeValueAsString(ItemMapper.toItemDto(item1)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1.getName())))
                .andExpect(jsonPath("$.description", is(item1.getDescription())))
                .andExpect(jsonPath("$.available", is(item1.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(nullValue())));
    }

    @Test
    @DisplayName("Поиск вещей по тексту")
    void getItemsByText() throws Exception {
        List<Item> items = List.of(item1, item2);
        when(itemService.getItemsByText(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(items);

        List<ItemDto> itemDtoList = ItemMapper.toItemDtoList(items);
        mvc.perform(get("/items/search")
                        .header(X_SHARER_USER_ID, 1L)
                        .param("text", "сувен")
                        .param("from", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoList)));
    }

    @Test
    @DisplayName("Добавление отзыва пользователем")
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(comment1);

        mvc.perform(post("/items/{itemId}/comment", comment1.getItem().getId())
                        .header(X_SHARER_USER_ID, item1.getOwner().getId())
                        .content(mapper.writeValueAsString(CommentMapper.toCommentDto(comment1)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment1.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment1.getText())))
                .andExpect(jsonPath("$.authorName", is(comment1.getAuthor().getName())))
                .andExpect(jsonPath("$.created", is(comment1.getCreated().toString())));
    }
}