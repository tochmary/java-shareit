package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.entity.Comment;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.entity.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    ItemRepository mockItemRepository;
    @Mock
    BookingRepository mockBookingRepository;
    @Mock
    CommentRepository mockCommentRepository;
    @Mock
    UserService mockUserService;
    @Mock
    ItemRequestService mockItemRequestService;

    ItemServiceImpl itemServiceImpl;

    private final static User USER_1 = new User(1L, "Maria", "maria_smart@mail.ru");
    private final static User USER_2 = new User(2L, "Ivan", "ivan_humble@mail.ru");

    private final static ItemRequest ITEM_REQUEST_1 = new ItemRequest(1L, "ищу сувенир", USER_2, LocalDateTime.now().plusSeconds(30));
    private final static ItemRequest ITEM_REQUEST_2 = new ItemRequest(2L, "ищу сувенир1", USER_2, LocalDateTime.now().plusSeconds(40));

    private final static Item ITEM_1 = new Item(1L, "Магнит", "сувенир1", true, USER_1, null);
    private final static Item ITEM_2 = new Item(2L, "Открытка", "сувенир2", true, USER_1, ITEM_REQUEST_1);

    private final static Comment COMMENT_1 = new Comment(1L, "комментарий1", ITEM_1, USER_2, LocalDateTime.now());


    @BeforeEach
    void setUp() {
        itemServiceImpl = new ItemServiceImpl(mockItemRepository,
                mockBookingRepository,
                mockCommentRepository,
                mockUserService,
                mockItemRequestService);
    }

    @Test
    @DisplayName("Получение списка вещей владельца")
    void getItemsByUserId() {
        List<Item> sourceItems = List.of(ITEM_1, ITEM_2);
        Mockito
                .when(mockItemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(sourceItems);

        List<Item> targetItems = itemServiceImpl.getItemsByUserId(USER_1.getId());
        Assertions.assertEquals(sourceItems.size(), targetItems.size());
        checkFields(sourceItems, targetItems);
    }

    @Test
    @DisplayName("Получение списка вещей владельца(пагинация)")
    void getItemsByUserIdWithPagination() {
        List<Item> sourceItems = List.of(ITEM_1, ITEM_2);
        Mockito
                .when(mockItemRepository.findItemsByOwnerId(anyLong(), any()))
                .thenReturn(new PageImpl<>(sourceItems));

        List<Item> targetItems = itemServiceImpl.getItemsByUserId(USER_1.getId(), 0, 20);
        Assertions.assertEquals(sourceItems.size(), targetItems.size());
        checkFields(sourceItems, targetItems);
    }

    @Test
    @DisplayName("Просмотр вещи")
    void getItemId() {
        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(ITEM_1));

        Item targetItem = itemServiceImpl.getItemId(USER_1.getId(), ITEM_1.getId());
        checkFields(targetItem, ITEM_1);
    }

    @Test
    @DisplayName("Добавление вещи")
    void addItem() {
        Item newItem = copyItem(ITEM_2);
        newItem.setId(null);
        newItem.setOwner(null);
        newItem.setRequest(null);
        Mockito
                .when(mockUserService.getUserById(anyLong()))
                .thenReturn(USER_1);
        Mockito
                .when(mockItemRequestService.getItemRequest(anyLong(), anyLong()))
                .thenReturn(ITEM_REQUEST_1);
        Mockito
                .when(mockItemRepository.save(any()))
                .thenReturn(ITEM_2);

        Item targetItem = itemServiceImpl.addItem(USER_1.getId(), newItem, ITEM_REQUEST_1.getId());
        checkFields(targetItem, ITEM_2);
    }

    @Test
    @DisplayName("Обновление вещи")
    void updateItem() {
        String newName = ITEM_2.getName() + "1";
        String newDescription = ITEM_2.getDescription() + "1";
        Boolean newAvailable = !ITEM_2.getAvailable();
        Item sourceItem = copyItem(ITEM_2);
        sourceItem.setName(newName);
        sourceItem.setDescription(newDescription);
        sourceItem.setAvailable(newAvailable);
        sourceItem.setRequest(ITEM_REQUEST_2);

        Item newItem = new Item();
        newItem.setName(newName);
        newItem.setDescription(newDescription);
        newItem.setAvailable(newAvailable);
        newItem.setRequest(ITEM_REQUEST_2);

        Mockito
                .when(mockItemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(ITEM_2));
        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(ITEM_2));
        Mockito
                .when(mockItemRequestService.getItemRequest(anyLong(), anyLong()))
                .thenReturn(ITEM_REQUEST_1);
        Mockito
                .when(mockItemRepository.save(any()))
                .thenReturn(sourceItem);

        Item targetItem = itemServiceImpl.updateItem(USER_1.getId(), ITEM_2.getId(), newItem, ITEM_REQUEST_2.getId());
        checkFields(targetItem, sourceItem);
    }

    @Test
    @DisplayName("Обновление имени вещи")
    void updateItemName() {
        String newName = ITEM_2.getName() + "1";
        Item sourceItem = copyItem(ITEM_2);
        sourceItem.setName(newName);

        Item newItem = new Item();
        newItem.setName(newName);

        Mockito
                .when(mockItemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(ITEM_2));
        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(ITEM_2));
        Mockito
                .when(mockItemRepository.save(any()))
                .thenReturn(sourceItem);

        Item targetItem = itemServiceImpl.updateItem(USER_1.getId(), ITEM_2.getId(), newItem, null);
        checkFields(targetItem, sourceItem);
    }

    @Test
    @DisplayName("Обновление описания вещи")
    void updateItemDescription() {
        String newDescription = ITEM_2.getDescription() + "1";
        Item sourceItem = copyItem(ITEM_2);
        sourceItem.setDescription(newDescription);

        Item newItem = new Item();
        newItem.setDescription(newDescription);

        Mockito
                .when(mockItemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(ITEM_2));
        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(ITEM_2));
        Mockito
                .when(mockItemRepository.save(any()))
                .thenReturn(sourceItem);

        Item targetItem = itemServiceImpl.updateItem(USER_1.getId(), ITEM_2.getId(), newItem, null);
        checkFields(targetItem, sourceItem);
    }

    @Test
    @DisplayName("Обновление статуса вещи")
    void updateItemAvailable() {
        Boolean newAvailable = !ITEM_2.getAvailable();
        Item sourceItem = copyItem(ITEM_2);
        sourceItem.setAvailable(newAvailable);

        Item newItem = new Item();
        newItem.setAvailable(newAvailable);

        Mockito
                .when(mockItemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(ITEM_2));
        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(ITEM_2));
        Mockito
                .when(mockItemRepository.save(any()))
                .thenReturn(sourceItem);

        Item targetItem = itemServiceImpl.updateItem(USER_1.getId(), ITEM_2.getId(), newItem, null);
        checkFields(targetItem, sourceItem);
    }

    @Test
    @DisplayName("Обновление запроса вещи")
    void updateItemRequest() {
        Item sourceItem = copyItem(ITEM_2);
        sourceItem.setRequest(ITEM_REQUEST_2);

        Item newItem = new Item();

        Mockito
                .when(mockItemRepository.findItemsByOwnerId(anyLong()))
                .thenReturn(List.of(ITEM_2));
        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(ITEM_2));
        Mockito
                .when(mockItemRequestService.getItemRequest(anyLong(), anyLong()))
                .thenReturn(ITEM_REQUEST_1);
        Mockito
                .when(mockItemRepository.save(any()))
                .thenReturn(sourceItem);

        Item targetItem = itemServiceImpl.updateItem(USER_1.getId(), ITEM_2.getId(), newItem, ITEM_REQUEST_2.getId());
        checkFields(targetItem, sourceItem);
    }

    @Test
    @DisplayName("Поиск вещей по тексту")
    void getItemsByText() {
        List<Item> sourceItems = List.of(ITEM_1, ITEM_2);
        Mockito
                .when(mockItemRepository.getItemsByText(anyString(), any()))
                .thenReturn(new PageImpl<>(sourceItems));

        List<Item> targetItems = itemServiceImpl.getItemsByText(USER_1.getId(), "сувенир", 0, 20);
        Assertions.assertEquals(sourceItems.size(), targetItems.size());
        checkFields(sourceItems, targetItems);
    }

    @Test
    @DisplayName("Добавление отзыва")
    void addComment() {
        Mockito
                .when(mockBookingRepository.findByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of(new Booking()));
        Mockito
                .when(mockUserService.getUserById(anyLong()))
                .thenReturn(USER_2);
        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(ITEM_2));
        Mockito
                .when(mockCommentRepository.save(any()))
                .thenReturn(COMMENT_1);

        Comment targetComment = itemServiceImpl.addComment(USER_2.getId(), ITEM_2.getId(), COMMENT_1);
        assertThat(targetComment.getId(), equalTo(COMMENT_1.getId()));
        assertThat(targetComment.getText(), equalTo(COMMENT_1.getText()));
        assertThat(targetComment.getItem(), equalTo(COMMENT_1.getItem()));
        assertThat(targetComment.getAuthor(), equalTo(COMMENT_1.getAuthor()));
        assertThat(targetComment.getCreated(), equalTo(COMMENT_1.getCreated()));
    }

    @Test
    @DisplayName("Получения отзывов о вещи")
    void getCommentsByItemId() {
        List<Comment> sourceComments = List.of(COMMENT_1);
        Mockito
                .when(mockCommentRepository.findAllByItemIdOrderByCreatedDesc(anyLong()))
                .thenReturn(sourceComments);

        List<Comment> targetComments = itemServiceImpl.getCommentsByItemId(ITEM_2.getId());
        Assertions.assertEquals(sourceComments.size(), targetComments.size());
        for (Comment comment : sourceComments) {
            assertThat(targetComments, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("text", equalTo(comment.getText())),
                    hasProperty("item", equalTo(comment.getItem())),
                    hasProperty("author", equalTo(comment.getAuthor())),
                    hasProperty("created", equalTo(comment.getCreated()))
            )));
        }
    }

    @Test
    @DisplayName("Получения вещей по запросу")
    void getItemsByRequestId() {
        List<Item> sourceItems = List.of(ITEM_2);
        Mockito
                .when(mockItemRepository.findItemsByRequestId(anyLong()))
                .thenReturn(sourceItems);

        List<Item> targetItems = itemServiceImpl.getItemsByRequestId(ITEM_REQUEST_1.getId());
        Assertions.assertEquals(sourceItems.size(), targetItems.size());
        checkFields(sourceItems, targetItems);
    }

    private void checkFields(Item targetItem, Item sourceItem) {
        assertThat(targetItem.getId(), equalTo(sourceItem.getId()));
        assertThat(targetItem.getName(), equalTo(sourceItem.getName()));
        assertThat(targetItem.getDescription(), equalTo(sourceItem.getDescription()));
        assertThat(targetItem.getAvailable(), equalTo(sourceItem.getAvailable()));
        assertThat(targetItem.getOwner(), equalTo(sourceItem.getOwner()));
        assertThat(targetItem.getRequest(), equalTo(sourceItem.getRequest()));
    }

    private void checkFields(List<Item> sourceItems, List<Item> targetItems) {
        for (Item item : sourceItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("available", equalTo(item.getAvailable())),
                    hasProperty("owner", equalTo(item.getOwner())),
                    hasProperty("request", equalTo(item.getRequest()))
            )));
        }
    }

    private Item copyItem(Item item) {
        Item newItem = new Item();
        newItem.setId(item.getId());
        newItem.setName(item.getName());
        newItem.setDescription(item.getDescription());
        newItem.setAvailable(item.getAvailable());
        newItem.setOwner(item.getOwner());
        newItem.setRequest(item.getRequest());
        return newItem;
    }
}