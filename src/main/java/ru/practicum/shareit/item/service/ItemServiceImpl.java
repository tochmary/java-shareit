package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.BadRequestException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public List<Item> getItemsByUserId(long userId) {
        log.debug("Получение списка вещей владельца с userId={}", userId);
        return itemRepository.findItemsByOwnerId(userId);
    }

    @Override
    public Item getItemId(long userId, long itemId) {
        log.debug("Просмотр вещи с itemId={}", itemId);
        userService.checkUser(userId);
        return getItemById(itemId);
    }

    @Override
    @Transactional
    public Item addItem(long userId, Item item) {
        log.debug("Добавление вещи {} пользователем с userId={}", item, userId);
        userService.checkUser(userId);
        item.setOwner(userService.getUserById(userId));
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(long userId, long itemId, Item item) {
        log.debug("Обновление вещи {} с itemId={} пользователем с userId={}", item, itemId, userId);
        userService.checkUser(userId);
        if (isExistItemIdForUserId(userId, itemId)) {
            throw new NotFoundException("У Пользователя с id " + userId + " не существует item c id " + itemId + "!");
        }
        Item itemNew = getItemById(itemId);
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
        log.debug("Поиск вещей с текстом={} в названии и описании пользователем с userId={}", text, userId);
        userService.checkUser(userId);
        return itemRepository.getItemsByText(text);
    }

    @Override
    @Transactional
    public Comment addComment(long userId, long itemId, Comment comment) {
        log.debug("Добавление отзыва {} пользователем с userId={} о вещи с itemId={}", comment, userId, itemId);
        if (bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("У Пользователя с userId=" + userId + " нет завершенных бронирований " +
                    "вещи c itemId=" + itemId + "!");
        }
        User author = userService.getUserById(userId);
        comment.setAuthor(author);
        Item item = getItemById(itemId);
        comment.setItem(item);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByItemId(long itemId) {
        log.debug("Получения отзывов о вещи с itemId={}", itemId);
        return commentRepository.findAllByItemIdOrderByCreatedDesc(itemId);
    }

    private boolean isExistItemIdForUserId(long userId, long itemId) {
        return itemRepository.findItemsByOwnerId(userId).stream().noneMatch(i -> i.getId() == itemId);
    }

    private Item getItemById(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещи с itemId=" + itemId + " не существует!")
        );
    }
}
