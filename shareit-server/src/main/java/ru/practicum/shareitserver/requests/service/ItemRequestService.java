package ru.practicum.shareitserver.requests.service;

import ru.practicum.shareitserver.requests.model.entity.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addItemRequest(long userId, ItemRequest itemRequest);

    List<ItemRequest> getItemRequestsByUserId(long userId);

    List<ItemRequest> getItemRequestsAll(long userId, Integer from, Integer size);

    ItemRequest getItemRequest(long userId, long requestId);

    void checkItemRequest(long requestId);
}
