package ru.practicum.shareitserver.requests.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareitserver.requests.model.entity.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findItemRequestByRequestorId(long userId);

    Page<ItemRequest> findAll(Pageable pageable);

    Page<ItemRequest> findAllByRequestorIdNot(long userId, Pageable pageable);
}
