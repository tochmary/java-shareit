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

    @Query("select it " +
            "from Item as it "+
            "where it.available = true "+
            "and (lower(it.name) like lower(concat('%', :text, '%')) " +
              "or lower(it.description) like lower(concat('%', :text, '%')))")
    List<Item> getItemsByText(@Param("text") String text);

    //List<Item> findItemsByAvailableIsTrueAndNameIsLikeIgnoreCaseOrAvailableIsTrueAndDescriptionIsLikeIgnoreCase(String text1, String text2);

    /*List<Item> getItemsByUserId(long userId);

    Item addItem(long userId, Item item);

    Item updateItem(long userId, Item item);

    Optional<Item> getItemId(long itemId);

    List<Item> getItemsByText(String text);*/
}
