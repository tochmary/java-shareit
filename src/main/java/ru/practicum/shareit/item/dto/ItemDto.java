package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.requests.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * id — уникальный идентификатор вещи;
 * name — краткое название;
 * description — развёрнутое описание;
 * available — статус о том, доступна или нет вещь для аренды;
 * owner — владелец вещи;
 * request — если вещь была создана по запросу другого пользователя, то в этом
 * поле будет храниться ссылка на соответствующий запрос.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    //private ItemRequest request;
    private Long requestId;
}
