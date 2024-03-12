package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemDataForRequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponseWithItem;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoJsonTest {

    @Autowired
    private JacksonTester<RequestDtoResponseWithItem> jsonRequestDtoResponseWithMD;

    @Test
    void testRequestDtoResponseWithMD() throws Exception {
        RequestDtoResponseWithItem requestDto = RequestDtoResponseWithItem.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.of(2023, 1, 1, 1, 1, 1))
                .items(Arrays.asList(
                        ItemDataForRequestDto.builder()
                                .id(1L)
                                .name("Item 1")
                                .description("Item 1 description")
                                .available(true)
                                .requestId(1L)
                                .build(),
                        ItemDataForRequestDto.builder()
                                .id(2L)
                                .name("Item 2")
                                .description("Item 2 description")
                                .available(true)
                                .requestId(1L)
                                .build(),
                        ItemDataForRequestDto.builder()
                                .id(3L)
                                .name("Item 3")
                                .description("Item 3 description")
                                .available(true)
                                .requestId(1L)
                                .build()))
                .build();

        JsonContent<RequestDtoResponseWithItem> result = jsonRequestDtoResponseWithMD.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-01T01:01:01");
        assertThat(result).extractingJsonPathArrayValue("$.items[*].id")
                .containsExactly(1, 2, 3);
        assertThat(result).extractingJsonPathArrayValue("$.items[*].name")
                .containsExactly("Item 1", "Item 2", "Item 3");
        assertThat(result).extractingJsonPathArrayValue("$.items[*].description")
                .containsExactly("Item 1 description", "Item 2 description", "Item 3 description");
        assertThat(result).extractingJsonPathArrayValue("$.items[*].available")
                .containsExactly(true, true, true);
        assertThat(result).extractingJsonPathArrayValue("$.items[*].requestId")
                .containsExactly(1, 1, 1);
    }
}