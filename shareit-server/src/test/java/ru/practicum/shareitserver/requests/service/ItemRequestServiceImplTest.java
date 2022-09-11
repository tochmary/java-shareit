package ru.practicum.shareitserver.requests.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareitserver.requests.model.entity.ItemRequest;
import ru.practicum.shareitserver.requests.repository.ItemRequestRepository;
import ru.practicum.shareitserver.user.model.entity.User;
import ru.practicum.shareitserver.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository mockIemRequestRepository;
    @Mock
    UserService mockUserService;

    ItemRequestServiceImpl itemRequestService;

    private static final User USER_1 = new User(1L, "Maria", "maria_smart@mail.ru");
    private static final User USER_2 = new User(2L, "Ivan", "ivan_humble@mail.ru");

    private static final ItemRequest ITEM_REQUEST_1 = new ItemRequest(1L, "ищу дом", USER_1, LocalDateTime.now());
    private static final ItemRequest ITEM_REQUEST_2 = new ItemRequest(2L, "ищу сувенир", USER_1, LocalDateTime.now().plusSeconds(30));

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(mockIemRequestRepository, mockUserService);
    }

    @Test
    @DisplayName("Добавление запроса")
    void addItemRequest() {
        ItemRequest newItemRequest = copyItemRequest(ITEM_REQUEST_1);
        newItemRequest.setId(null);
        Mockito
                .when(mockUserService.getUserById(anyLong()))
                .thenReturn(USER_1);
        Mockito
                .when(mockIemRequestRepository.save(any()))
                .thenReturn(ITEM_REQUEST_1);

        ItemRequest targetItemRequest = itemRequestService.addItemRequest(USER_1.getId(), newItemRequest);
        assertThat(targetItemRequest.getId(), equalTo(ITEM_REQUEST_1.getId()));
        assertThat(targetItemRequest.getDescription(), equalTo(ITEM_REQUEST_1.getDescription()));
        assertThat(targetItemRequest.getRequestor(), equalTo(ITEM_REQUEST_1.getRequestor()));
        assertThat(targetItemRequest.getCreated(), equalTo(ITEM_REQUEST_1.getCreated()));
    }

    @Test
    @DisplayName("Получение списка запросов владельца")
    void getItemRequestsByUserId() {
        List<ItemRequest> sourceItemRequests = List.of(ITEM_REQUEST_1, ITEM_REQUEST_2);
        Mockito
                .when(mockIemRequestRepository.findItemRequestByRequestorId(anyLong()))
                .thenReturn(sourceItemRequests);

        List<ItemRequest> targetItemRequests = itemRequestService.getItemRequestsByUserId(USER_1.getId());
        Assertions.assertEquals(sourceItemRequests.size(), targetItemRequests.size());
        for (ItemRequest sourceItemRequest : sourceItemRequests) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceItemRequest.getDescription())),
                    hasProperty("requestor", equalTo(sourceItemRequest.getRequestor())),
                    hasProperty("created", equalTo(sourceItemRequest.getCreated()))
            )));
        }
    }

    @Test
    @DisplayName("Получение списка всех не своих запросов для пользователя")
    void getItemRequestsAll() {
        List<ItemRequest> sourceItemRequests = List.of(ITEM_REQUEST_1, ITEM_REQUEST_2);
        Page<ItemRequest> sourceItemRequestPages = new PageImpl<>(sourceItemRequests);
        Mockito
                .when(mockIemRequestRepository.findAllByRequestorIdNot(anyLong(), any()))
                .thenReturn(sourceItemRequestPages);

        List<ItemRequest> targetItemRequests = itemRequestService.getItemRequestsAll(USER_2.getId(), 0, 20);
        Assertions.assertEquals(sourceItemRequests.size(), targetItemRequests.size());
        for (ItemRequest sourceItemRequest : sourceItemRequests) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceItemRequest.getDescription())),
                    hasProperty("requestor", equalTo(sourceItemRequest.getRequestor())),
                    hasProperty("created", equalTo(sourceItemRequest.getCreated()))
            )));
        }
    }

    @Test
    @DisplayName("Получение запроса")
    void getItemRequest() {
        Mockito
                .when(mockIemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(ITEM_REQUEST_1));

        ItemRequest targetItemRequest = itemRequestService.getItemRequest(USER_1.getId(), ITEM_REQUEST_1.getId());
        assertThat(targetItemRequest.getId(), equalTo(ITEM_REQUEST_1.getId()));
        assertThat(targetItemRequest.getDescription(), equalTo(ITEM_REQUEST_1.getDescription()));
        assertThat(targetItemRequest.getRequestor(), equalTo(ITEM_REQUEST_1.getRequestor()));
        assertThat(targetItemRequest.getCreated(), equalTo(ITEM_REQUEST_1.getCreated()));
    }

    private ItemRequest copyItemRequest(ItemRequest itemRequest) {
        ItemRequest newItemRequest = new ItemRequest();
        newItemRequest.setId(itemRequest.getId());
        newItemRequest.setDescription(itemRequest.getDescription());
        newItemRequest.setRequestor(itemRequest.getRequestor());
        newItemRequest.setCreated(itemRequest.getCreated());
        return newItemRequest;
    }
}