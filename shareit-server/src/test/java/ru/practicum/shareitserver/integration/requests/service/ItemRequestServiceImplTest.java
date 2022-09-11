package ru.practicum.shareitserver.integration.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareitserver.requests.mapper.ItemRequestMapper;
import ru.practicum.shareitserver.requests.model.dto.ItemRequestDto;
import ru.practicum.shareitserver.requests.model.entity.ItemRequest;
import ru.practicum.shareitserver.requests.service.ItemRequestService;
import ru.practicum.shareitserver.user.mapper.UserMapper;
import ru.practicum.shareitserver.user.model.dto.UserDto;
import ru.practicum.shareitserver.user.model.entity.User;

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
public class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestService service;

    @Test
    @DisplayName("Получение списка всех не своих запросов для пользователя")
    void getItemRequestsAll() {
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
        User user1 = queryUser.setParameter("email", email1).getSingleResult();
        User user2 = queryUser.setParameter("email", email2).getSingleResult();
        User user3 = queryUser.setParameter("email", email3).getSingleResult();

        //Создание запросов
        ItemRequest itemRequest1 = ItemRequestMapper.toItemRequest(makeItemRequestDto("ищу сувенир"));
        itemRequest1.setRequestor(user1);
        em.persist(itemRequest1);
        ItemRequest itemRequest2 = ItemRequestMapper.toItemRequest(makeItemRequestDto("ищу дом"));
        itemRequest2.setRequestor(user2);
        em.persist(itemRequest2);
        ItemRequest itemRequest3 = ItemRequestMapper.toItemRequest(makeItemRequestDto("ищу косметику"));
        itemRequest3.setRequestor(user3);
        em.persist(itemRequest3);
        em.flush();

        List<ItemRequest> sourceItemRequests = List.of(itemRequest1, itemRequest3);

        //WHEN
        List<ItemRequest> targetItemRequests = service.getItemRequestsAll(user2.getId(), 0, 20);

        //THEN
        assertThat(targetItemRequests, hasSize(sourceItemRequests.size()));
        for (ItemRequest sourceItemRequest : sourceItemRequests) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceItemRequest.getDescription())),
                    hasProperty("requestor", allOf(
                            hasProperty("id", equalTo(sourceItemRequest.getRequestor().getId())),
                            hasProperty("name", equalTo(sourceItemRequest.getRequestor().getName())),
                            hasProperty("email", equalTo(sourceItemRequest.getRequestor().getEmail()))
                    ))
            )));
        }
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
}
