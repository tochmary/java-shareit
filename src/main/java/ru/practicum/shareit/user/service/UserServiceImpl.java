package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с id = " + userId + " не существует!")
        );
    }

    @Override
    @Transactional
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user, Long userId) {
        User userNew = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с id = " + userId + " не существует!")
        );
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
        userRepository.deleteById(userId);
    }

    @Override
    public void checkUser(long userId) {
        if (!isUserExist(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не существует!");
        }
    }

    private boolean isUserExist(long userId) {
        return userRepository.findById(userId).isPresent();
    }
}
