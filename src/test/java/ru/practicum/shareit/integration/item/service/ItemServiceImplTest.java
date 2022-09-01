package ru.practicum.shareit.integration.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.entity.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceImplTest {

    private final EntityManager em;
    private final ItemService service;

    @Test
    @DisplayName("Получение списка вещей владельца")
    void getItemsByUserId() {
        //GIVEN
        //Создание пользователей
        String email1 = "maria_smart@mail.ru";
        String email2 = "ivan_humble@mail.ru";
        String email3 = "daria_funny@mail.ru";
        List<UserDto> sourceUsers = List.of(
                makeUserDto("Maria", email1),
                makeUserDto("Ivan", email2),
                makeUserDto("Daria", email3)
        );
        for (UserDto user : sourceUsers) {
            User entity = UserMapper.toUser(user);
            em.persist(entity);
        }
        em.flush();

        TypedQuery<User> queryUser = em.createQuery("select u from User u where u.email = :email", User.class);
        User owner1 = queryUser.setParameter("email", email1).getSingleResult();
        User requestor = queryUser.setParameter("email", email2).getSingleResult();
        User owner2 = queryUser.setParameter("email", email3).getSingleResult();

        //Создание запроса
        ItemRequestDto sourceItemRequest = makeItemRequestDto("ищу сувенир1");
        ItemRequest itemRequest1 = ItemRequestMapper.toItemRequest(sourceItemRequest);
        itemRequest1.setRequestor(requestor);
        em.persist(itemRequest1);
        em.flush();

        //Создание вещей
        List<ItemDto> sourceItems1 = List.of(
                makeItemDto("Магнит", "сувенир1", true, itemRequest1.getId()),
                makeItemDto("Открытка", "сувенир2", true, null)
        );
        saveItemsInDB(sourceItems1, owner1, em);
        List<ItemDto> sourceItems2 = List.of(
                makeItemDto("Конфеты", "сувенир3", true, null),
                makeItemDto("Косметика", "сувенир4", true, null)
        );
        saveItemsInDB(sourceItems2, owner2, em);


        //WHEN
        List<Item> targetItems = service.getItemsByUserId(owner1.getId(), 0, 20);

        //THEN
        assertThat(targetItems, hasSize(sourceItems1.size()));
        for (ItemDto sourceItem : sourceItems1) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("description", equalTo(sourceItem.getDescription())),
                    hasProperty("available", equalTo(sourceItem.getAvailable()))
            )));
            if (sourceItem.getRequestId() != null) {
                TypedQuery<ItemRequest> queryItemRequest = em.createQuery("select i from ItemRequest i where i.id = :id", ItemRequest.class);
                ItemRequest itemRequest = queryItemRequest.setParameter("id", sourceItem.getRequestId()).getSingleResult();
                assertThat(targetItems, hasItem(allOf(
                        hasProperty("request", allOf(
                                hasProperty("id", equalTo(itemRequest.getId())),
                                hasProperty("description", equalTo(itemRequest.getDescription())),
                                hasProperty("requestor", equalTo(itemRequest.getRequestor())),
                                hasProperty("created", equalTo(itemRequest.getCreated()))
                        ))
                )));
            } else {
                assertThat(targetItems, hasItem(allOf(hasProperty("request", nullValue()))));
            }
        }
    }

    private void saveItemsInDB(List<ItemDto> sourceItems, User owner2, EntityManager em) {
        for (ItemDto item : sourceItems) {
            Item entity = ItemMapper.toItem(item);
            entity.setOwner(owner2);
            if (item.getRequestId() != null) {
                TypedQuery<ItemRequest> queryItemRequest = em.createQuery("select i from ItemRequest i where i.id = :id", ItemRequest.class);
                ItemRequest itemRequest = queryItemRequest.setParameter("id", item.getRequestId()).getSingleResult();
                entity.setRequest(itemRequest);
            }
            em.persist(entity);
        }
        em.flush();
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private ItemRequestDto makeItemRequestDto(String description) {
        ItemRequestDto itemRequest = new ItemRequestDto();
        itemRequest.setDescription(description);
        return itemRequest;
    }

    private ItemDto makeItemDto(String name,
                                String description,
                                Boolean available,
                                Long requestId) {
        ItemDto item = new ItemDto();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setRequestId(requestId);
        return item;
    }
}
