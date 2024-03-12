package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utilitary.Constants.HEADER_USER_ID;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemControllerTest {

    private final ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private final MockMvc mvc;
    private ItemDto item1;
    private ItemDto itemDtoResponse;
    private ItemDto itemDtoUpdate;

    @BeforeEach
    public void setUp() {
        item1 = ItemDto.builder()
                .name("item test")
                .description("item test description")
                .available(Boolean.TRUE)
                .build();
        itemDtoResponse = ItemDto.builder()
                .id(1L)
                .name(item1.getName())
                .description(item1.getDescription())
                .available(Boolean.TRUE)
                .build();
        itemDtoUpdate = ItemDto.builder()
                .name("update item test")
                .description("update test description")
                .build();
    }

    @Test
    public void createItem() throws Exception {
        when(itemService.create(any(ItemDto.class), anyLong())).thenReturn(itemDtoResponse);

        mvc.perform(post("/items")
                        .header(HEADER_USER_ID, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemDtoResponse)));
    }

    @SneakyThrows
    @Test
    public void createItemWithIncorrectUserId() {
        mvc.perform(post("/items")
                        .header(HEADER_USER_ID, 0)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).create(any(ItemDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    public void createItemWithIncorrectAvailable() {
        item1.setAvailable(null);

        mvc.perform(post("/items")
                        .header(HEADER_USER_ID, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).create(any(ItemDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    public void updateItem() {
        itemDtoResponse.setName(itemDtoUpdate.getName());
        itemDtoResponse.setDescription(itemDtoUpdate.getDescription());

        when(itemService.update(any(ItemDto.class), anyLong(), anyLong())).thenReturn(itemDtoResponse);
        mvc.perform(patch("/items/1")
                        .header(HEADER_USER_ID, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemDtoResponse)));
    }

    @SneakyThrows
    @Test
    public void updateItemWithIncorrectUserId() {
        mvc.perform(patch("/items/1")
                        .header(HEADER_USER_ID, 0)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).update(any(ItemDto.class), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    public void updateItemWithIncorrectItemId() {
        mvc.perform(patch("/items/0")
                        .header(HEADER_USER_ID, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).update(any(ItemDto.class), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    public void updateItemWithIncorrectName() {
        itemDtoUpdate.setName("    updated name");

        mvc.perform(patch("/items/0")
                        .header(HEADER_USER_ID, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).update(any(ItemDto.class), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    public void updateItemWithIncorrectDescription() {
        itemDtoUpdate.setDescription("   updated description");

        mvc.perform(patch("/items/0")
                        .header(HEADER_USER_ID, 1)
                        .content(objectMapper.writeValueAsString(item1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).update(any(ItemDto.class), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    public void getItemById() {
        when(itemService.getItemsByOwner(anyLong(), anyLong())).thenReturn(itemDtoResponse);

        mvc.perform(get("/items/1")
                        .header(HEADER_USER_ID, 1))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemDtoResponse)));
    }

    @SneakyThrows
    @Test
    public void getItemByIdWithIncorrectUserId() {
        mvc.perform(get("/items/1")
                        .header(HEADER_USER_ID, 0))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).getItemsByOwner(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    public void getItemByIncorrectId() {
        mvc.perform(get("/items/0")
                        .header(HEADER_USER_ID, 1))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).getItemsByOwner(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    public void getPersonalItems() {
        var itemListDto = List.of(itemDtoResponse);

        when(itemService.getItemsByOwner(anyLong(), anyInt(), anyInt())).thenReturn(itemListDto);
        mvc.perform(get("/items")
                        .param("from", "1")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", 1))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemListDto)));
    }

    @SneakyThrows
    @Test
    public void getPersonalItemsWithIncorrectUserId() {
        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "1")
                        .header(HEADER_USER_ID, 0))
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).getItemsByOwner(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    public void getPersonalItemsWithIncorrectParamFrom() {
        mvc.perform(get("/items")
                        .param("from", "-1")
                        .param("size", "1")
                        .header(HEADER_USER_ID, 1))
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).getItemsByOwner(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    public void getPersonalItemsWithIncorrectParamSize() {
        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "99999")
                        .header(HEADER_USER_ID, 1))
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).getItemsByOwner(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    public void getFoundItems() {
        var itemListDto = List.of(itemDtoResponse);

        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(itemListDto);
        mvc.perform(get("/items/search")
                        .param("from", "1")
                        .param("size", "1")
                        .param("text", "description"))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(itemListDto)));
    }

    @SneakyThrows
    @Test
    public void getFoundItemsWitchIncorrectParamFrom() {
        mvc.perform(get("/items/search")
                        .param("from", "-1")
                        .param("size", "1")
                        .param("text", "description"))
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).search(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    public void getFoundItemsWitchIncorrectParamSize() {
        mvc.perform(get("/items/search")
                        .param("from", "0")
                        .param("size", "0")
                        .param("text", "description"))
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).search(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    public void addComment() {
        var comment = CommentDto.builder()
                .text("Nice!")
                .build();
        var commentDtoResponse = CommentDto.builder()
                .id(1L)
                .authorName(item1.getName())
                .text(comment.getText())
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDtoResponse);
        mvc.perform(post("/items/1/comment")
                        .header(HEADER_USER_ID, 1)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(commentDtoResponse)));
    }

    @SneakyThrows
    @Test
    public void addCommentWithEmptyText() {
        var comment = CommentDto.builder()
                .text("     ")
                .build();

        mvc.perform(post("/items/1/comment")
                        .header(HEADER_USER_ID, 1)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }

    @SneakyThrows
    @Test
    public void addCommentWithIncorrectItemId() {
        var comment = CommentDto.builder()
                .text("     ")
                .build();

        mvc.perform(post("/items/0/comment")
                        .header(HEADER_USER_ID, 1)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }

    @SneakyThrows
    @Test
    public void addCommentWithIncorrectUserId() {
        var comment = CommentDto.builder()
                .text("     ")
                .build();

        mvc.perform(post("/items/1/comment")
                        .header(HEADER_USER_ID, 0)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(itemService, times(0)).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }
}