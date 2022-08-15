package ru.practicum.shareit.item.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.dto.BookingDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * id — уникальный идентификатор вещи;
 * name — краткое название;
 * description — развёрнутое описание;
 * available — статус о том, доступна или нет вещь для аренды;
 * lastBooking — последнее бронирование;
 * nextBooking - следующее бронирование;
 * comments - отзывы;
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemFullDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;
}
