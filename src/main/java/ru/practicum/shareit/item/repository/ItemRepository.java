package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> getItemsByUserId(long userId);

    Item addItem(long userId, Item item);

    Item updateItem(long userId, Item item);

    Optional<Item> getItemId(long itemId);

    List<Item> getItemsByText(String text);
}
