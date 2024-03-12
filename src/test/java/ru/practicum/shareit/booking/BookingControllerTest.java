package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utilitary.Constants.HEADER_USER_ID;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private final BookingService bookingService;
    private static BookingDto bookingDto;
    private List<BookingOutDto> bookingListDto;
    private static BookingOutDto bookingOutDto;

    @BeforeEach
    public void setUp() {
        bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(bookingDto.getItemId())
                .name("test item")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("test name")
                .build();
        bookingOutDto = BookingOutDto.builder()
                .id(1L)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(itemDto)
                .booker(userDto)
                .status(bookingDto.getStatus())
                .build();
    }

    @Test
    @SneakyThrows
    public void addBooking() {
        when(bookingService.addBooking(any(BookingDto.class), anyLong())).thenReturn(bookingOutDto);
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingOutDto)));
    }

    @Test
    @SneakyThrows
    public void addBookingWithIncorrectBookerId() {
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 0))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(bookingService, times(0)).addBooking(any(BookingDto.class), anyLong());
    }

    @Test
    @SneakyThrows
    public void confirmationBooking() {
        bookingOutDto.setStatus(Status.APPROVED);

        when(bookingService.confirmationBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingOutDto);
        mvc.perform((patch("/bookings/1"))
                        .header(HEADER_USER_ID, 1)
                        .param("approved", "true"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingOutDto)));
        bookingOutDto.setStatus(Status.WAITING);
    }

    @Test
    @SneakyThrows
    public void confirmationBookingWitchIncorrectUserId() {
        mvc.perform((patch("/bookings/1"))
                        .header(HEADER_USER_ID, 0)
                        .param("approved", "true"))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(bookingService, times(0)).confirmationBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    public void getBookingById() {
        when(bookingService.getBookingByIdAndBookerId(anyLong(), anyLong())).thenReturn(bookingOutDto);
        mvc.perform(get("/bookings/1")
                        .header(HEADER_USER_ID, 1))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingOutDto)));
    }

    @Test
    @SneakyThrows
    public void getBookingByIncorrectBookingIdForOwnerAndBooker() {
        mvc.perform(get("/bookings/0")
                        .header(HEADER_USER_ID, 1))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(bookingService, times(0)).getBookingByIdAndBookerId(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    public void getBookingByIdWithIncorrectUserIdForOwnerAndBooker() {
        mvc.perform(get("/bookings/1")
                        .header(HEADER_USER_ID, 0))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(bookingService, times(0)).getBookingByIdAndBookerId(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    public void getBookingByIdWithOutUserIdForOwnerAndBooker() {
        mvc.perform(get("/bookings/1"))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(bookingService, times(0)).getBookingByIdAndBookerId(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUser() {
        bookingListDto = List.of(bookingOutDto);
        when(bookingService.getAllBrookingByBookerId(any(Pageable.class), anyLong(), anyString())).thenReturn(bookingListDto);

        mvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, 1)
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingListDto)));
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUserWithIncorrectUserId() {
        mvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, 0)
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(bookingService, times(0))
                .getAllBrookingByBookerId(any(Pageable.class), anyLong(), anyString());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUserWithIncorrectParamFrom() {
        mvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, 1)
                        .param("from", "-1")
                        .param("size", "2"))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(bookingService, times(0))
                .getAllBrookingByBookerId(any(Pageable.class), anyLong(), anyString());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUserWithIncorrectParamSize() {
        mvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, 1)
                        .param("from", "0")
                        .param("size", "10000"))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(bookingService, times(0))
                .getAllBrookingByBookerId(any(Pageable.class), anyLong(), anyString());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForItemsUserWithIncorrectUserId() {
        mvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, 0)
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(bookingService, times(0))
                .getAllBookingsForAllItemsByOwnerId(any(Pageable.class), anyLong(), anyString());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForItemsUserWithIncorrectParamFrom() {
        mvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, 1)
                        .param("from", "-1")
                        .param("size", "2"))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(bookingService, times(0))
                .getAllBookingsForAllItemsByOwnerId(any(Pageable.class), anyLong(), anyString());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForItemsUserWithIncorrectParamSize() {
        mvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, 1)
                        .param("from", "0")
                        .param("size", "10000"))
                .andDo(print())
                .andExpectAll(status().isBadRequest());
        verify(bookingService, times(0))
                .getAllBookingsForAllItemsByOwnerId(any(Pageable.class), anyLong(), anyString());
    }
}