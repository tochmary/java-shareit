package ru.practicum.shareit.user.repository;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl {
    private final UserRepository userRepository;

    public UserRepositoryImpl(@Lazy UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
