package ru.practicum.shareitgateway.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * POST /requests — добавить новый запрос вещи.
 * Основная часть запроса — текст запроса, где пользователь описывает, какая именно вещь ему нужна.
 * <p>
 * GET /requests — получить список своих запросов вместе с данными об ответах на них.
 * Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате:
 * id вещи, название, id владельца.
 * Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи.
 * Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
 * <p>
 * GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
 * С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
 * на которые они могли бы ответить. Запросы сортируются по дате создания: от более новых к более старым.
 * Результаты должны возвращаться постранично.
 * Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0,
 * и size — количество элементов для отображения.
 * <p>
 * GET /requests/{requestId} — получить данные об одном конкретном запросе
 * вместе с данными об ответах на него в том же формате, что и в эндпоинте GET /requests.
 * Посмотреть данные об отдельном запросе может любой пользователь.
 */
@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Добавление нового запроса {} пользователем с userId={}", itemRequestDto, userId);
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение списка запросов вместе с данными об ответах на них для владельца с userId={}", userId);
        return itemRequestClient.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение списка запросов, созданных другими пользователями для userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getItemRequestsAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long requestId) {
        log.info("Получение о запросе requestId={} вместе с данными об ответах для userId={}",
                requestId, userId);
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
