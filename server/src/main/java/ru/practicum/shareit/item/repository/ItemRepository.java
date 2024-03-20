package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByUserId(long userId);

    List<Item> findByUserIdOrderById(Long userId, PageRequest pageRequest);

    @Query("select i from Item i " +
            "   where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "   or upper(i.description) like upper(concat('%', ?1, '%')) " +
            "   and i.available = true ")
    List<Item> search(String text, PageRequest pageRequest);

    List<Item> findByUserId(Long userId);

    @Query("SELECT i.id FROM Item AS i " +
            "JOIN User As u ON i.user.id=u.id " +
            "WHERE i.user.id = ?1")
    List<Long> findAllItemIdByOwnerId(Long ownerId);

    Boolean existsItemByUserId(Long ownerId);
}
