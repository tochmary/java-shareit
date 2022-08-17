package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.entity.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Получение списка пользователей");
        List<User> userList = userService.getUsers();
        return UserMapper.getUserDtoList(userList);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Получение пользователя с userId={}", userId);
        User user = userService.getUserById(userId);
        return UserMapper.toUserDto(user);
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Добавление пользователя {}", userDto);
        User user = UserMapper.toUser(userDto);
        user = userService.addUser(user);
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Добавление пользователя {} с userId={}", userDto, userId);
        User user = UserMapper.toUser(userDto);
        user = userService.updateUser(user, userId);
        return UserMapper.toUserDto(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Удаление пользователя с userId={}", userId);
        userService.deleteUser(userId);
    }
}