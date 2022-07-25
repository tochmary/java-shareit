package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * id — уникальный идентификатор пользователя;
 * name — имя или логин пользователя;
 * email — адрес электронной почты (учтите, что два пользователя не могут
 * иметь одинаковый адрес электронной почты).
 */
@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}