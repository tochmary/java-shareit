package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static List<ItemDto> toItemDtoList(List<Item> itemList) {
        return itemList.stream()
                .map(ItemMapper::toItemDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    public static ItemFullDto toItemFullDto(Item item,
                                            Booking lastBooking,
                                            Booking nextBooking,
                                            List<Comment> comments) {
        return new ItemFullDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking == null ? null : BookingMapper.toBookingDto(lastBooking),
                nextBooking == null ? null : BookingMapper.toBookingDto(nextBooking),
                comments == null ? null : CommentMapper.toCommentDtoList(comments)
        );
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

}
