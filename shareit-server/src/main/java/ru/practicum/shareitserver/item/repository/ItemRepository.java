package ru.practicum.shareitserver.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareitserver.item.model.entity.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwnerId(long userId);

    Page<Item> findItemsByOwnerId(long userId, Pageable pageable);

    @Query("select it " +
            "from Item as it " +
            "where it.available = true " +
            "and (lower(it.name) like lower(concat('%', :text, '%')) " +
            "or lower(it.description) like lower(concat('%', :text, '%')))")
    Page<Item> getItemsByText(@Param("text") String text, Pageable pageable);

    List<Item> findItemsByRequestId(long requestId);
}
