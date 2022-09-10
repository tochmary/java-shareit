package ru.practicum.shareitgateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.item.dto.CommentDto;
import ru.practicum.shareitgateway.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareitgateway.common.Constants.X_SHARER_USER_ID;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(X_SHARER_USER_ID) long userId,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получение списка вещей владельца с userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(X_SHARER_USER_ID) long userId,
                                              @PathVariable long itemId) {
        log.info("Просмотр вещи с itemId={} пользователем с userId={}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(X_SHARER_USER_ID) long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Добавление вещи {} пользователем с userId={}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Обновление вещи {} с itemId={} пользователем с userId={}", itemDto, itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                 @RequestParam String text,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Поиск вещей с текстом={} пользователем с userId={}, from={}, size={}\"", text, userId, from, size);
        return itemClient.getItemsByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_USER_ID) long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавление отзыва {} пользователем с userId={} о вещи с itemId={}", commentDto, userId, itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
