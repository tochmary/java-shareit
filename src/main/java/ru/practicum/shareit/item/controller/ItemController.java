package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<Item> itemList = itemService.getItemsByUserId(userId);
        return ItemMapper.toItemDtoList(itemList);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long itemId) {
        Item item = itemService.getItemId(userId, itemId);
        return ItemMapper.toItemDto(item);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        //User user = userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, userId);
        item = itemService.addItem(userId, item);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        //User user = userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, userId);
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
}
