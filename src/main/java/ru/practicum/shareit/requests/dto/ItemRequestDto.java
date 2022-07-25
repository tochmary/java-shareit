package ru.practicum.shareit.requests.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * id — уникальный идентификатор запроса;
 * description — текст запроса, содержащий описание требуемой вещи;
 * requestor — пользователь, создавший запрос;
 * created — дата и время создания запроса..
 */
@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requestor;
    private LocalDate created;
}
