package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUserById(Long userId);

    User addUser(User user);

    User updateUser(User user, Long userId);

    void deleteUser(long userId);

    void checkUser(long userId);
}
