package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.getUserById(userId).orElseThrow(
                () -> new NotFoundException("Пользователя с id = " + userId + " не существует!")
        );
    }

    @Override
    public User addUser(User user) {
        checkEmail(user);
        return userRepository.addUser(user);
    }

    @Override
    public User updateUser(User user, Long userId) {
        user.setId(userId);
        checkEmail(user);
        return userRepository.updateUser(user);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public void checkUser(long userId) {
        if (!userRepository.isUserExist(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не существует!");
        }
    }

    private void checkEmail(User user) {
        if (userRepository.isExistEmail(user)) {
            throw new ValidationException("Данный email уже существует для другого пользователя!");
        }
    }
}
