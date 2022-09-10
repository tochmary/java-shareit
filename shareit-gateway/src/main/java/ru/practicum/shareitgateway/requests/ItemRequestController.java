package ru.practicum.shareitgateway.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareitgateway.common.Constants.X_SHARER_USER_ID;

@Slf4j
@Validated
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Добавление нового запроса {} пользователем с userId={}", itemRequestDto, userId);
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(X_SHARER_USER_ID) long userId) {
        log.info("Получение списка запросов вместе с данными об ответах на них для владельца с userId={}", userId);
        return itemRequestClient.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsAll(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Получение списка запросов, созданных другими пользователями для userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getItemRequestsAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(X_SHARER_USER_ID) long userId,
                                                 @PathVariable long requestId) {
        log.info("Получение о запросе requestId={} вместе с данными об ответах для userId={}",
                requestId, userId);
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
