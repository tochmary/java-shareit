package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemFullDto;
import ru.practicum.shareit.item.model.entity.Comment;
import ru.practicum.shareit.item.model.entity.Item;

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
        ItemFullDto itemFullDto = new ItemFullDto();
        itemFullDto.setId(item.getId());
        itemFullDto.setName(item.getName());
        itemFullDto.setDescription(item.getDescription());
        itemFullDto.setAvailable(item.getAvailable());
        itemFullDto.setLastBooking(lastBooking == null ? null : BookingMapper.toBookingDto(lastBooking));
        itemFullDto.setNextBooking(nextBooking == null ? null : BookingMapper.toBookingDto(nextBooking));
        itemFullDto.setComments(comments == null ? null : CommentMapper.toCommentDtoList(comments));
        return itemFullDto;
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
