package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private long id = 1;
    private final Map<Long, Map<Long, Item>> items = new HashMap<>();

    @Override
    public List<Item> getItemsByUserId(long userId) {
        return new ArrayList<>(items.getOrDefault(userId, Collections.emptyMap()).values());
    }

    @Override
    public Item addItem(long userId, Item item) {
        long itemId = generateId();
        item.setId(itemId);
        items.compute(userId, (uId, userItems) -> {
            if (userItems == null) {
                userItems = new HashMap<>();
            }
            userItems.put(itemId, item);
            return userItems;
        });
        log.info("Добавлен item: {}", item);
        return item;
    }

    @Override
    public Item updateItem(long userId, Item item) {
        long itemId = item.getId();
        Map<Long, Item> userItems = items.get(userId);
        Item itemNew = userItems.get(itemId);
        if (item.getAvailable() != null) {
            itemNew.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            itemNew.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemNew.setDescription(item.getDescription());
        }
        userItems.put(itemId, itemNew);
        log.info("Обновлен item: {}", itemNew);
        return itemNew;
    }

    @Override
    public Optional<Item> getItemId(long itemId) {
        return items.values()
                .stream()
                .flatMap(userItems -> userItems.values().stream())
                .filter(item -> item.getId() == itemId)
                .findAny();
    }

    @Override
    public List<Item> getItemsByText(String text) {
        List<Item> itemList = items.values()
                .stream()
                .flatMap(userItems -> userItems.values().stream())
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
        log.info("Найдено item: {}", itemList.size());
        return itemList;
    }

    private long generateId() {
        return id++;
    }
}
