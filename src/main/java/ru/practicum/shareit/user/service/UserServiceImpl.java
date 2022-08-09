package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
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
        //return userRepository.getUsers();
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        /*return userRepository.getUserById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с id = " + userId + " не существует!")
        );*/
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с id = " + userId + " не существует!")
        );
    }

    @Override
    @Transactional
    public User addUser(User user) {
        //checkEmail(user);
        //return userRepository.addUser(user);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user, Long userId) {
        //user.setId(userId);
        //checkEmail(user);
        //return userRepository.updateUser(user);
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
        //userRepository.deleteUser(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public void checkUser(long userId) {
        if (!isUserExist(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не существует!");
        }
    }
/*
    private void checkEmail(User user) {
        if (userRepository.isExistEmail(user)) {
            throw new ValidationException("Данный email уже существует для другого пользователя!");
        }
    }
*/
    private boolean isUserExist(long userId) {
        return userRepository.findById(userId).isPresent();
    }
}
