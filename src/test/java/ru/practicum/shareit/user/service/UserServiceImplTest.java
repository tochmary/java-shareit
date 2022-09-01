package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository mockUserRepository;

    UserServiceImpl userServiceImpl;

    private static final User USER_1 = new User(1L, "Maria", "maria_smart@mail.ru");
    private static final User USER_2 = new User(2L, "Ivan", "ivan_humble@mail.ru");

    @BeforeEach
    void setUp() {
        userServiceImpl = new UserServiceImpl(mockUserRepository);
    }

    @Test
    @DisplayName("Получение списка всех пользователей")
    void getUsers() {
        List<User> sourceUsers = List.of(USER_1, USER_2);
        Mockito.when(mockUserRepository.findAll()).thenReturn(sourceUsers);

        List<User> targetUsers = userServiceImpl.getUsers();
        Assertions.assertEquals(sourceUsers.size(), targetUsers.size());
        for (User sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    @DisplayName("Получение пользователя по id")
    void getUserById() {
        Long id = USER_1.getId();
        Mockito
                .when(mockUserRepository.findById(id))
                .thenReturn(Optional.of(USER_1));

        User targetUser = userServiceImpl.getUserById(id);
        assertThat(targetUser.getId(), equalTo(USER_1.getId()));
        assertThat(targetUser.getName(), equalTo(USER_1.getName()));
        assertThat(targetUser.getEmail(), equalTo(USER_1.getEmail()));
    }

    @Test
    @DisplayName("Добавление пользователя")
    void addUser() {
        User newUser = copyUser(USER_1);
        newUser.setId(null);
        Mockito
                .when(mockUserRepository.save(any(User.class)))
                .thenReturn(USER_1);

        User targetUser = userServiceImpl.addUser(newUser);
        assertThat(targetUser.getId(), equalTo(USER_1.getId()));
        assertThat(targetUser.getName(), equalTo(USER_1.getName()));
        assertThat(targetUser.getEmail(), equalTo(USER_1.getEmail()));
    }

    @Test
    @DisplayName("Обновление имени и email пользователя по id")
    void updateUser() {
        Long id = USER_1.getId();
        String newName = "Maria1";
        String newEmail = "maria1_smart@mail.ru";
        User newUser = new User(null, newName, newEmail);

        Mockito
                .when(mockUserRepository.findById(id))
                .thenReturn(Optional.of(USER_1));
        Mockito
                .when(mockUserRepository.save(any(User.class)))
                .thenReturn(new User(id, newName, newEmail));

        User targetUser = userServiceImpl.updateUser(newUser, id);
        assertThat(targetUser.getId(), equalTo(id));
        assertThat(targetUser.getName(), equalTo(newName));
        assertThat(targetUser.getEmail(), equalTo(newEmail));
    }

    @Test
    @DisplayName("Обновление имени пользователя по id")
    void updateUserName() {
        Long id = USER_1.getId();
        String newName = "maria1_smart@mail.ru";
        User newUser = new User(null, newName, null);

        Mockito
                .when(mockUserRepository.findById(id))
                .thenReturn(Optional.of(USER_1));
        Mockito
                .when(mockUserRepository.save(any(User.class)))
                .thenReturn(new User(id, newName, USER_1.getEmail()));

        User targetUser = userServiceImpl.updateUser(newUser, id);
        assertThat(targetUser.getId(), equalTo(id));
        assertThat(targetUser.getName(), equalTo(newName));
        assertThat(targetUser.getEmail(), equalTo(USER_1.getEmail()));
    }

    @Test
    @DisplayName("Обновление email пользователя по id")
    void updateUserEmail() {
        Long id = USER_1.getId();
        String newEmail = "maria1_smart@mail.ru";
        User newUser = new User(null, null, newEmail);

        Mockito
                .when(mockUserRepository.findById(id))
                .thenReturn(Optional.of(USER_1));
        Mockito
                .when(mockUserRepository.save(any(User.class)))
                .thenReturn(new User(id, USER_1.getName(), newEmail));

        User targetUser = userServiceImpl.updateUser(newUser, id);
        assertThat(targetUser.getId(), equalTo(id));
        assertThat(targetUser.getName(), equalTo(USER_1.getName()));
        assertThat(targetUser.getEmail(), equalTo(newEmail));
    }

    @Test
    @DisplayName("Удаление пользователя по id")
    void deleteUser() {
        Long id = USER_1.getId();

        userServiceImpl.deleteUser(id);

        Mockito
                .verify(mockUserRepository, Mockito.times(1))
                .deleteById(id);
    }

    private User copyUser(User user) {
        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        return newUser;
    }
}