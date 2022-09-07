package ru.practicum.shareitserver.requests.mapper;

import ru.practicum.shareitserver.item.mapper.ItemMapper;
import ru.practicum.shareitserver.item.model.entity.Item;
import ru.practicum.shareitserver.requests.model.dto.ItemRequestDto;
import ru.practicum.shareitserver.requests.model.entity.ItemRequest;
import ru.practicum.shareitserver.user.mapper.UserMapper;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestor(UserMapper.toUserDto(itemRequest.getRequestor()));
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(items == null ? null : ItemMapper.toItemDtoList(items));
        return itemRequestDto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }
}
