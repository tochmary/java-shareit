package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

//@Slf4j
@Repository
public class UserRepositoryImpl {
    private final UserRepository userRepository;

    public UserRepositoryImpl(@Lazy UserRepository userRepository){
        this.userRepository = userRepository;
    }
/*
    private long id = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        Long userId = generateId();
        user.setId(userId);
        users.put(userId, user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public Boolean isExistEmail(User user) {
        return users.values().stream()
                .filter(u -> !Objects.equals(u.getId(), user.getId()))
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));
    }

    @Override
    public User updateUser(User user) {
        Long userId = user.getId();
        User userNew = users.get(userId);
        if (user.getEmail() != null) {
            userNew.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userNew.setName(user.getName());
        }
        users.put(userId, userNew);
        log.info("Обновлен пользователь: {}", userNew);
        return userNew;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        Optional<User> user = Optional.ofNullable(users.get(userId));
        log.info("Найден пользователь: {}", user);
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
        log.info("Удален пользователь с id: {}", userId);
    }

    private long generateId() {
        return id++;
    }
 */
}
