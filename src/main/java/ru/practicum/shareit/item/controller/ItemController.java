package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.Validation;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemFullDto;
import ru.practicum.shareit.item.model.entity.Comment;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;

    @GetMapping
    public List<ItemFullDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получение списка вещей владельца с userId={}", userId);
        log.info("from={}, size={}", from, size);
        Validation.checkRequestParam("from", from);
        Validation.checkRequestParam("size", size);
        List<Item> itemList = itemService.getItemsByUserId(userId, from, size);
        return itemList.stream()
                .map(item -> getItemWithBookDto(bookingService, userId, item))
                .sorted(Comparator.comparing(ItemFullDto::getId))
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemFullDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long itemId) {
        log.info("Просмотр вещи с itemId={} пользователем с userId={}", itemId, userId);
        Item item = itemService.getItemId(userId, itemId);
        return getItemWithBookDto(bookingService, userId, item);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        log.info("Добавление вещи {} пользователем с userId={}", itemDto, userId);
        Item item = ItemMapper.toItem(itemDto);
        item = itemService.addItem(userId, item, itemDto.getRequestId());
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Обновление вещи {} с itemId={} пользователем с userId={}", itemDto, itemId, userId);
        Item item = ItemMapper.toItem(itemDto);
        item = itemService.updateItem(userId, itemId, item, itemDto.getRequestId());
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam String text,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "20") Integer size) {
        log.info("Поиск вещей с текстом={} пользователем с userId={}", text, userId);
        log.info("from={}, size={}", from, size);
        Validation.checkRequestParam("from", from);
        Validation.checkRequestParam("size", size);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemList = itemService.getItemsByText(userId, text, from, size);
        return ItemMapper.toItemDtoList(itemList);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавление отзыва {} пользователем с userId={} о вещи с itemId={}", commentDto, userId, itemId);
        Comment comment = CommentMapper.toComment(commentDto);
        comment = itemService.addComment(userId, itemId, comment);
        return CommentMapper.toCommentDto(comment);
    }

    private ItemFullDto getItemWithBookDto(BookingService bookingService, long userId, Item item) {
        Booking lastBooking = bookingService.getLastBookingByItemId(userId, item);
        Booking nextBooking = bookingService.getNextBookingByItemId(userId, item);
        List<Comment> comments = itemService.getCommentsByItemId(item.getId());
        return ItemMapper.toItemFullDto(item, lastBooking, nextBooking, comments);
    }
}
