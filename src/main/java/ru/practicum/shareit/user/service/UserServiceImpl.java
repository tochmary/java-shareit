package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        log.debug("Получение списка всех пользователей");
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        log.debug("Получение пользователя с userId={}", userId);
        return getUserByUserId(userId);
    }

    @Override
    @Transactional
    public User addUser(User user) {
        log.debug("Добавление пользователя {}", user);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user, Long userId) {
        log.debug("Добавление пользователя {} с userId={}", user, userId);
        User userNew = getUserByUserId(userId);
        if (user.getEmail() != null) {
            userNew.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userNew.setName(user.getName());
        }
        return userRepository.save(userNew);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        log.debug("Удаление пользователя с userId={}", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public void checkUser(long userId) {
        log.debug("Проверка существования пользователя с userId={}", userId);
        getUserByUserId(userId);
    }

    private User getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с userId=" + userId + " не существует!")
        );
    }
}
