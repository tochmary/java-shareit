package ru.practicum.shareitgateway.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareitgateway.item.dto.ItemDto;
import ru.practicum.shareitgateway.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * id — уникальный идентификатор запроса;
 * description — текст запроса, содержащий описание требуемой вещи;
 * requestor — пользователь, создавший запрос;
 * created — дата и время создания запроса.
 * items - список вещей по данному запросу
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotBlank
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}
