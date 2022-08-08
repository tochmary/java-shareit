package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getUsers();

    User addUser(User user);

    Boolean isExistEmail(User user);

    User updateUser(User user);

    Optional<User> getUserById(Long userId);

    void deleteUser(long userId);
}
