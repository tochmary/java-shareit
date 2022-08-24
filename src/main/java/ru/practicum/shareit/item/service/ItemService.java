package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.entity.Comment;
import ru.practicum.shareit.item.model.entity.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItemsByUserId(long userId);

    Item getItemId(long userId, long itemId);

    Item addItem(long userId, Item item, Long requestId);

    Item updateItem(long userId, long itemId, Item item, Long requestId);

    List<Item> getItemsByText(long userId, String text);

    Comment addComment(long userId, long itemId, Comment comment);

    List<Comment> getCommentsByItemId(long itemId);

    List<Item> getItemsByRequestId(long requestId);
}
