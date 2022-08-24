package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.model.entity.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addItemRequest(long userId, ItemRequest itemRequest);

    List<ItemRequest> getItemRequestsByUserId(long userId);

    List<ItemRequest> getItemRequestsAll(long userId, Integer from, Integer size);

    ItemRequest getItemRequest(long userId, long requestId);
}
