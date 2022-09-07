package ru.practicum.shareitserver.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.common.exception.NotFoundException;
import ru.practicum.shareitserver.requests.model.entity.ItemRequest;
import ru.practicum.shareitserver.requests.repository.ItemRequestRepository;
import ru.practicum.shareitserver.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequest addItemRequest(long userId, ItemRequest itemRequest) {
        log.debug("Добавление запроса {} пользователем с userId={}", itemRequest, userId);
        userService.checkUser(userId);
        itemRequest.setRequestor(userService.getUserById(userId));
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getItemRequestsByUserId(long userId) {
        log.debug("Получение списка запросов владельца с userId={}", userId);
        userService.checkUser(userId);
        return itemRequestRepository.findItemRequestByRequestorId(userId);
    }

    @Override
    public List<ItemRequest> getItemRequestsAll(long userId, Integer from, Integer size) {
        log.debug("Получение списка всех не своих запросов для userId={}. Со страницы {} в количестве {}", userId, from, size);
        userService.checkUser(userId);
        log.info("from={}, size={}", from, size);
        PageRequest pr = PageRequest.of(from / size, size);
        return itemRequestRepository.findAllByRequestorIdNot(userId, pr).toList();
    }

    @Override
    public ItemRequest getItemRequest(long userId, long requestId) {
        log.debug("Получение запроса requestId={} для userId={}", requestId, userId);
        userService.checkUser(userId);
        return findItemRequestById(requestId);
    }

    @Override
    public void checkItemRequest(long requestId) {
        log.debug("Проверка существования запроса с requestId={}", requestId);
        findItemRequestById(requestId);
    }

    private ItemRequest findItemRequestById(long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запроса с requestId=" + requestId + " не существует!")
        );
    }
}
