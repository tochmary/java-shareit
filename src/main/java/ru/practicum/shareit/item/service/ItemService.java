package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemService {
    List<Item> getItemsByUserId(long userId);

    Item getItemId(long userId, long itemId);

    Item addItem(long userId, Item item);

    Item updateItem(long userId, long itemId, Item item);

    List<Item> getItemsByText(long userId, String text);

    Comment addComment(long userId, long itemId, Comment comment);

    Set<Comment> getCommentsByItemId(long itemId);
}
