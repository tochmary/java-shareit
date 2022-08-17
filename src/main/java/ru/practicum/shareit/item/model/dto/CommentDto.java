package ru.practicum.shareit.item.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * id — уникальный идентификатор комментария;
 * text — содержимое комментария;
 * author — имя автора комментария;
 * created — дата создания комментария.
 */
@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
