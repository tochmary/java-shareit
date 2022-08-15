package ru.practicum.shareit.item.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.shareit.requests.model.entity.ItemRequest;
import ru.practicum.shareit.user.model.entity.User;

import javax.persistence.*;

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
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ItemRequest request;
}
