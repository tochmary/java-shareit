package ru.practicum.shareit.requests.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.dto.ItemFullDto;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.entity.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 POST /requests — добавить новый запрос вещи.
 Основная часть запроса — текст запроса, где пользователь описывает, какая именно вещь ему нужна.

 GET /requests — получить список своих запросов вместе с данными об ответах на них.
 Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате:
 id вещи, название, id владельца.
 Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи.
 Запросы должны возвращаться в отсортированном порядке от более новых к более старым.

 GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
 С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
 на которые они могли бы ответить. Запросы сортируются по дате создания: от более новых к более старым.
 Результаты должны возвращаться постранично.
 Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0,
 и size — количество элементов для отображения.

 GET /requests/{requestId} — получить данные об одном конкретном запросе
 вместе с данными об ответах на него в том же формате, что и в эндпоинте GET /requests.
 Посмотреть данные об отдельном запросе может любой пользователь.
 */
@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Добавление нового запроса {} пользователем с userId={}", itemRequestDto, userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest = itemRequestService.addItemRequest(userId, itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение списка запросов вместе с данными об ответах на них для владельца с userId={}", userId);
        List<ItemRequest> ItemRequestList = itemRequestService.getItemRequestsByUserId(userId);
        return ItemRequestList.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest))
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequestsAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получение списка запросов, созданных другими пользователями для userId={}, from={}, size={}", userId, from, size);
        List<ItemRequest> ItemRequestList = itemRequestService.getItemRequestsAll(userId, from, size);
        return ItemRequestList.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest))
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long requestId) {
        log.info("Получение о запросе requestId={} вместе с данными об ответах для userId={}",
                requestId, userId);
        ItemRequest ItemRequest = itemRequestService.getItemRequest(userId, requestId);
        return ItemRequestMapper.toItemRequestDto(ItemRequest);
    }
}
