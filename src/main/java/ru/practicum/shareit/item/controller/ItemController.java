package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;

    @GetMapping
    public List<ItemFullDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<Item> itemList = itemService.getItemsByUserId(userId);
        return itemList.stream()
                .map(item -> getItemWithBookDto(bookingService, userId, item))
                .sorted(Comparator.comparing(ItemFullDto::getId))
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemFullDto getItemByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long itemId) {
        Item item = itemService.getItemId(userId, itemId);
        return getItemWithBookDto(bookingService, userId, item);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item = itemService.addItem(userId, item);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item = itemService.updateItem(userId, itemId, item);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemList = itemService.getItemsByText(userId, text);
        return ItemMapper.toItemDtoList(itemList);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
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
