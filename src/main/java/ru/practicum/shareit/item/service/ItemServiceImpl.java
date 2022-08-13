package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    public List<Item> getItemsByUserId(long userId) {
        //return itemRepository.getItemsByUserId(userId);
        return itemRepository.findItemsByOwnerId(userId);
    }

    @Override
    public Item getItemId(long userId, long itemId) {
        userService.checkUser(userId);
        /*return itemRepository.getItemId(itemId).orElseThrow(
                () -> new RuntimeException("Item с id = " + itemId + " не существует!")
        );*/
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item с id = " + itemId + " не существует!")
        );
    }

    @Override
    @Transactional
    public Item addItem(long userId, Item item) {
        userService.checkUser(userId);
        item.setOwner(userService.getUserById(userId));
        //return itemRepository.addItem(userId, item);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(long userId, long itemId, Item item) {
        userService.checkUser(userId);
        if (isExistItemIdForUserId(userId, itemId)) {
            throw new NotFoundException("У Пользователя с id " + userId + " не существует item c id " + itemId + "!");
        }
        //item.setId(itemId);
        //return itemRepository.updateItem(userId, item);
        Item itemNew = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item с id = " + itemId + " не существует!")
        );
        if (item.getAvailable() != null) {
            itemNew.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            itemNew.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemNew.setDescription(item.getDescription());
        }
        return itemRepository.save(itemNew);
    }

    @Override
    public List<Item> getItemsByText(long userId, String text) {
        log.info("getItemsByText {} {}", userId, text);
        userService.checkUser(userId);
        //return itemRepository.getItemsByText(text);
        return itemRepository.getItemsByText(text);
    }

    private boolean isExistItemIdForUserId(long userId, long itemId) {
        return itemRepository.findItemsByOwnerId(userId).stream().noneMatch(i -> i.getId() == itemId);
    }
}
