package ru.practicum.shareitserver.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.common.exception.BadRequestException;
import ru.practicum.shareitserver.common.exception.NotFoundException;
import ru.practicum.shareitserver.item.model.entity.Comment;
import ru.practicum.shareitserver.item.model.entity.Item;
import ru.practicum.shareitserver.item.repository.CommentRepository;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.requests.service.ItemRequestService;
import ru.practicum.shareitserver.user.service.UserService;

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
    private final ItemRequestService itemRequestService;

    @Override
    public List<Item> getItemsByUserId(long userId) {
        log.debug("Получение списка вещей владельца с userId={}", userId);
        return itemRepository.findItemsByOwnerId(userId);
    }

    @Override
    public List<Item> getItemsByUserId(long userId, Integer from, Integer size) {
        log.debug("Получение списка вещей владельца с userId={}", userId);
        log.info("from={}, size={}", from, size);
        PageRequest pr = PageRequest.of(from / size, size);
        return itemRepository.findItemsByOwnerId(userId, pr).toList();
    }

    @Override
    public Item getItemId(long userId, long itemId) {
        log.debug("Просмотр вещи с itemId={}", itemId);
        userService.checkUser(userId);
        return getItemById(itemId);
    }

    @Override
    @Transactional
    public Item addItem(long userId, Item item, Long requestId) {
        log.debug("Добавление вещи {} пользователем с userId={}, requestId={}", item, userId, requestId);
        userService.checkUser(userId);
        item.setOwner(userService.getUserById(userId));
        if (requestId != null) {
            itemRequestService.checkItemRequest(requestId);
            item.setRequest(itemRequestService.getItemRequest(userId, requestId));
        }
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(long userId, long itemId, Item item, Long requestId) {
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
        if (requestId != null) {
            itemRequestService.checkItemRequest(requestId);
            itemNew.setRequest(itemRequestService.getItemRequest(userId, requestId));
        }
        return itemRepository.save(itemNew);
    }

    @Override
    public List<Item> getItemsByText(long userId, String text, Integer from, Integer size) {
        log.debug("Поиск вещей с текстом={} в названии и описании пользователем с userId={}", text, userId);
        log.info("from={}, size={}", from, size);
        PageRequest pr = PageRequest.of(from / size, size);
        userService.checkUser(userId);
        return itemRepository.getItemsByText(text, pr).toList();
    }

    @Override
    @Transactional
    public Comment addComment(long userId, long itemId, Comment comment) {
        log.debug("Добавление отзыва {} пользователем с userId={} о вещи с itemId={}", comment, userId, itemId);
        if (bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("У Пользователя с userId=" + userId + " нет завершенных бронирований " +
                    "вещи c itemId=" + itemId + "!");
        }
        comment.setAuthor(userService.getUserById(userId));
        comment.setItem(getItemById(itemId));
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByItemId(long itemId) {
        log.debug("Получение отзывов о вещи с itemId={}", itemId);
        return commentRepository.findAllByItemIdOrderByCreatedDesc(itemId);
    }

    @Override
    public List<Item> getItemsByRequestId(long requestId) {
        log.debug("Получение вещей по запросу requestId={}", requestId);
        return itemRepository.findItemsByRequestId(requestId);
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
