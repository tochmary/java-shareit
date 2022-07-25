package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<Item> getItemsByUserId(long userId) {
        return itemRepository.getItemsByUserId(userId);
    }

    @Override
    public Item getItemId(long userId, long itemId) {
        userService.checkUser(userId);
        return itemRepository.getItemId(itemId).orElseThrow(
                () -> new RuntimeException("Item с id = " + itemId + " не существует!")
        );
    }

    @Override
    public Item addItem(long userId, Item item) {
        userService.checkUser(userId);
        item.setOwner(userService.getUserById(userId));
        return itemRepository.addItem(userId, item);
    }

    @Override
    public Item updateItem(long userId, long itemId, Item item) {
        userService.checkUser(userId);
        if (itemRepository.getItemsByUserId(userId).stream().noneMatch(i -> i.getId() == itemId)) {
            throw new NotFoundException("У Пользователя с id " + userId + " не существует item c id " + itemId + "!");
        }
        item.setId(itemId);
        return itemRepository.updateItem(userId, item);
    }

    @Override
    public List<Item> getItemsByText(long userId, String text) {
        userService.checkUser(userId);
        return itemRepository.getItemsByText(text);
    }
}
