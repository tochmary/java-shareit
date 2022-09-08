package ru.practicum.shareitgateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Получение списка пользователей");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Получение пользователя с userId={}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto,
                                             @PathVariable long userId) {
        log.info("Добавление пользователя {} с userId={}", userDto, userId);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("Удаление пользователя с userId={}", userId);
        return userClient.deleteUser(userId);
    }
}