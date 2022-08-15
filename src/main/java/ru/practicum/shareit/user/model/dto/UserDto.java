package ru.practicum.shareit.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * id — уникальный идентификатор пользователя;
 * name — имя или логин пользователя;
 * email — адрес электронной почты (учтите, что два пользователя не могут
 * иметь одинаковый адрес электронной почты).
 */
@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    @Email
    @NotBlank
    private String email;
}
