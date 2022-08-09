package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwnerId(long userId);

    @Query(value = "select it.* " +
            "from items as it "+
            "where it.is_available = true "+
            "and (it.name ilike '%'||:text||'%' or it.description ilike '%'||:text||'%')", nativeQuery = true)
    List<Item> getItemsByText(@Param("text") String text);

    //List<Item> findItemsByAvailableIsTrueAndNameIsLikeIgnoreCaseOrAvailableIsTrueAndDescriptionIsLikeIgnoreCase(String text1, String text2);

    /*List<Item> getItemsByUserId(long userId);

    Item addItem(long userId, Item item);

    Item updateItem(long userId, Item item);

    Optional<Item> getItemId(long itemId);

    List<Item> getItemsByText(String text);*/
}
