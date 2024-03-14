package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestPropertySource(properties = {"db.name=test"})
@Sql(scripts = {"file:src/main/resources/schema.sql"})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceTest extends Bookings {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private BookingDto booking1Dto;


    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setName("test name");
        user1.setEmail("test@test.ru");
        user2 = new User();
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");
        item1 = new Item();
        item1.setName("test item");
        item1.setDescription("test description1");
        item1.setAvailable(Boolean.TRUE);
        item1.setUser(user1);
        item2 = new Item();
        item2.setName("test item2");
        item2.setDescription("test description2");
        item2.setAvailable(Boolean.TRUE);
        item2.setUser(user2);
        booking1Dto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
    }

    @Test
    public void createAndGetBooking() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        var savedBooking = bookingService.addBooking(booking1Dto, user2.getId());
        var findBooking = bookingService
                .getBookingByIdAndBookerId(user2.getId(), savedBooking.getId());

        assertThat(savedBooking).usingRecursiveComparison().ignoringFields("start", "end")
                .isEqualTo(findBooking);
    }

    @Test
    public void addBookingWhenEndBeforeStart() {
        booking1Dto.setEnd(LocalDateTime.now().plusDays(1));
        booking1Dto.setStart(LocalDateTime.now().plusDays(2));
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        assertThatThrownBy(() -> bookingService.addBooking(booking1Dto, user2.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void addBookingWithNotExistingItem() {
        booking1Dto.setItemId(2L);
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        assertThatThrownBy(() -> bookingService.addBooking(booking1Dto, user2.getId()))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    public void addBookingWhenBookerIsOwner() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        assertThatThrownBy(() -> bookingService.addBooking(booking1Dto, user1.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void addBookingWhenNotExistingBooker() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        assertThatThrownBy(() -> bookingService.addBooking(booking1Dto, 99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void addBookingWithNotAvailableItem() {
        item1.setAvailable(Boolean.FALSE);
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        assertThatThrownBy(() -> bookingService.addBooking(booking1Dto, user2.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void confirmationBooking() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        var savedBooking = bookingService.addBooking(booking1Dto, user2.getId());
        var approvedBooking = bookingService.confirmationBooking(user1.getId(), savedBooking.getId(), true);
        var findBooking = bookingService.getBookingByIdAndBookerId(user2.getId(), savedBooking.getId());

        assertThat(approvedBooking).usingRecursiveComparison().isEqualTo(findBooking);
    }

    @Test
    public void rejectBooking() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        var savedBooking = bookingService.addBooking(booking1Dto, user2.getId());
        var approvedBooking = bookingService.confirmationBooking(user1.getId(), savedBooking.getId(), false);
        var findBooking = bookingService.getBookingByIdAndBookerId(user2.getId(), savedBooking.getId());

        assertThat(approvedBooking).usingRecursiveComparison().isEqualTo(findBooking);
    }

    @Test
    public void confirmationBookingWithNotExistingBooking() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        bookingService.addBooking(booking1Dto, user2.getId());

        assertThatThrownBy(() -> bookingService.confirmationBooking(user1.getId(), 99L, true))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    public void confirmationBookingWhenUserIsNotOwner() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        var savedBooking = bookingService.addBooking(booking1Dto, user2.getId());

        assertThatThrownBy(() -> bookingService.confirmationBooking(user2.getId(), savedBooking.getId(), true))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void getBookingWhenBookingNotFound() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        bookingService.addBooking(booking1Dto, user2.getId());

        assertThatThrownBy(() -> bookingService.getBookingByIdAndBookerId(99L, user2.getId()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void getBookingWhenUserIsNotOwnerOrBooker() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        var savedBooking = bookingService.addBooking(booking1Dto, user2.getId());
        assertThatThrownBy(() -> bookingService.getBookingByIdAndBookerId(savedBooking.getId(), 10L))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    public void getAllBookingForUserWhenStateIsAll() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBrookingByBookerId(PageRequest.of(0, 10), user2.getId(), "ALL");

        assertThat(findBookingList.size()).isEqualTo(10);
        List<Long> ids = findBookingList.stream().map(BookingOutDto::getId).collect(Collectors.toList());
        assertThat(ids).first().isEqualTo(futureBookingForItem2.getId());
        assertThat(ids).element(1).isEqualTo(futureBookingForItem1.getId());
        assertThat(ids).element(2).isEqualTo(rejectedBookingForItem2.getId());
        assertThat(ids).element(3).isEqualTo(rejectedBookingForItem1.getId());
        assertThat(ids).element(4).isEqualTo(waitingBookingForItem2.getId());
        assertThat(ids).element(5).isEqualTo(waitingBookingForItem1.getId());
        assertThat(ids).element(6).isEqualTo(currentBookingForItem2.getId());
        assertThat(ids).element(7).isEqualTo(currentBookingForItem1.getId());
        assertThat(ids).element(9).isEqualTo(pastBookingForItem1.getId());
        assertThat(ids).element(8).isEqualTo(pastBookingForItem2.getId());
    }

    @Test
    public void getAllBookingsForItemsUser() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForAllItemsByOwnerId(PageRequest.of(0, 10), user1.getId(), "ALL");

        assertThat(findBookingList.size()).isEqualTo(5);
        List<Long> ids = findBookingList.stream().map(BookingOutDto::getId).collect(Collectors.toList());
        assertThat(ids).first().isEqualTo(futureBookingForItem1.getId());
        assertThat(ids).element(1).isEqualTo(rejectedBookingForItem1.getId());
        assertThat(ids).element(2).isEqualTo(waitingBookingForItem1.getId());
        assertThat(ids).element(3).isEqualTo(currentBookingForItem1.getId());
        assertThat(ids).element(4).isEqualTo(pastBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForUserWhenStateIsCurrent() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBrookingByBookerId(PageRequest.of(0, 10), user2.getId(), "CURRENT");

        assertThat(findBookingList.size()).isEqualTo(2);
        List<Long> ids = findBookingList.stream().map(BookingOutDto::getId).collect(Collectors.toList());
        assertThat(ids).first().isEqualTo(currentBookingForItem2.getId());
        assertThat(ids).last().isEqualTo(currentBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsCurrent() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForAllItemsByOwnerId(PageRequest.of(0, 10), user1.getId(), "CURRENT");

        List<Long> ids = findBookingList.stream().map(BookingOutDto::getId).collect(Collectors.toList());
        assertThat(ids).singleElement().isEqualTo(currentBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForUserWhenStateIsPast() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBrookingByBookerId(PageRequest.of(0, 10), user2.getId(), "PAST");

        assertThat(findBookingList.size()).isEqualTo(2);
        List<Long> ids = findBookingList.stream().map(BookingOutDto::getId).collect(Collectors.toList());
        assertThat(ids).first().isEqualTo(pastBookingForItem2.getId());
        assertThat(ids).last().isEqualTo(pastBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsPast() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForAllItemsByOwnerId(PageRequest.of(0, 10), user1.getId(), "PAST");

        List<Long> ids = findBookingList.stream().map(BookingOutDto::getId).collect(Collectors.toList());
        assertThat(ids).singleElement().isEqualTo(pastBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForUserWhenStateIsFuture() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBrookingByBookerId(PageRequest.of(0, 10), user2.getId(), "Future");

        assertThat(findBookingList.size()).isEqualTo(6);
        List<Long> ids = findBookingList.stream().map(BookingOutDto::getId).collect(Collectors.toList());
        assertThat(ids).first().isEqualTo(futureBookingForItem2.getId());
        assertThat(ids).element(1).isEqualTo(futureBookingForItem1.getId());
        assertThat(ids).element(2).isEqualTo(rejectedBookingForItem2.getId());
        assertThat(ids).element(3).isEqualTo(rejectedBookingForItem1.getId());
        assertThat(ids).element(4).isEqualTo(waitingBookingForItem2.getId());
        assertThat(ids).element(5).isEqualTo(waitingBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsFuture() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForAllItemsByOwnerId(PageRequest.of(0, 10), user1.getId(), "Future");

        assertThat(findBookingList.size()).isEqualTo(3);
        List<Long> ids = findBookingList.stream().map(BookingOutDto::getId).collect(Collectors.toList());
        assertThat(ids).first().isEqualTo(futureBookingForItem1.getId());
        assertThat(ids).element(1).isEqualTo(rejectedBookingForItem1.getId());
        assertThat(ids).element(2).isEqualTo(waitingBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForUserWhenStateIsWaiting() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBrookingByBookerId(PageRequest.of(0, 10), user2.getId(), "waiting");

        assertThat(findBookingList.size()).isEqualTo(2);
        List<Long> ids = findBookingList.stream().map(BookingOutDto::getId).collect(Collectors.toList());
        assertThat(ids).first().isEqualTo(waitingBookingForItem2.getId());
        assertThat(ids).last().isEqualTo(waitingBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsWaiting() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForAllItemsByOwnerId(PageRequest.of(0, 10), user1.getId(), "waiting");

        List<Long> ids = findBookingList.stream().map(BookingOutDto::getId).collect(Collectors.toList());
        assertThat(ids).singleElement().isEqualTo(waitingBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForUserWhenStateIsRejected() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBrookingByBookerId(PageRequest.of(0, 10), user2.getId(), "rejected");

        assertThat(findBookingList.size()).isEqualTo(2);
        List<Long> ids = findBookingList.stream().map(BookingOutDto::getId).collect(Collectors.toList());
        assertThat(ids).first().isEqualTo(rejectedBookingForItem2.getId());
        assertThat(ids).last().isEqualTo(rejectedBookingForItem1.getId());
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsRejected() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForAllItemsByOwnerId(PageRequest.of(0, 10), user1.getId(), "rejected");

        List<Long> ids = findBookingList.stream().map(BookingOutDto::getId).collect(Collectors.toList());
        assertThat(ids).singleElement().isEqualTo(rejectedBookingForItem1.getId());
    }

    @Test
    public void getBookingListWithUnknownState() {
        userRepository.save(user1);
        assertThatThrownBy(() -> bookingService
                .getAllBrookingByBookerId(PageRequest.of(0, 10), user1.getId(), "qwe"))
                .isInstanceOf(IncorrectStatusException.class);
    }

    @Test
    public void getAllBookingsForUserWhenUserNotFound() {
        userRepository.save(user1);
        assertThatThrownBy(() -> bookingService
                .getAllBrookingByBookerId(PageRequest.of(0, 10), 50L, "ALL"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getAllBookingsForItemsUserWhenUserNotFound() {
        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        assertThatThrownBy(() -> bookingService
                .getAllBookingsForAllItemsByOwnerId(PageRequest.of(0, 10), 50L, "ALL"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getAllBookingsForItemsUserWhenUserNotExistingBooking() {
        userRepository.save(user1);
        assertThatThrownBy(() -> bookingService
                .getAllBookingsForAllItemsByOwnerId(PageRequest.of(0, 10), user1.getId(), "ALL"))
                .isInstanceOf(RuntimeException.class);
    }

    @SneakyThrows
    private void initializationItem2AndBookings() {

        currentBookingForItem1 = new Booking();
        currentBookingForItem1.setStart(LocalDateTime.now().minusDays(1));
        currentBookingForItem1.setEnd(LocalDateTime.now().plusDays(1));
        currentBookingForItem1.setItem(item1);
        currentBookingForItem1.setBooker(user2);
        currentBookingForItem1.setStatus(Status.APPROVED);

        Thread.sleep(50);

        currentBookingForItem2 = new Booking();
        currentBookingForItem2.setStart(LocalDateTime.now().minusDays(1));
        currentBookingForItem2.setEnd(LocalDateTime.now().plusDays(1));
        currentBookingForItem2.setItem(item2);
        currentBookingForItem2.setBooker(user2);
        currentBookingForItem2.setStatus(Status.APPROVED);

        Thread.sleep(50);

        pastBookingForItem1 = new Booking();
        pastBookingForItem1.setStart(LocalDateTime.now().minusDays(2));
        pastBookingForItem1.setEnd(LocalDateTime.now().minusDays(1));
        pastBookingForItem1.setItem(item1);
        pastBookingForItem1.setBooker(user2);
        pastBookingForItem1.setStatus(Status.APPROVED);

        Thread.sleep(50);

        pastBookingForItem2 = new Booking();
        pastBookingForItem2.setStart(LocalDateTime.now().minusDays(2));
        pastBookingForItem2.setEnd(LocalDateTime.now().minusDays(1));
        pastBookingForItem2.setItem(item2);
        pastBookingForItem2.setBooker(user2);
        pastBookingForItem2.setStatus(Status.APPROVED);

        Thread.sleep(50);

        futureBookingForItem1 = new Booking();
        futureBookingForItem1.setStart(LocalDateTime.now().plusDays(1));
        futureBookingForItem1.setEnd(LocalDateTime.now().plusDays(2));
        futureBookingForItem1.setItem(item1);
        futureBookingForItem1.setBooker(user2);
        futureBookingForItem1.setStatus(Status.APPROVED);

        Thread.sleep(50);

        futureBookingForItem2 = new Booking();
        futureBookingForItem2.setStart(LocalDateTime.now().plusDays(1));
        futureBookingForItem2.setEnd(LocalDateTime.now().plusDays(2));
        futureBookingForItem2.setItem(item2);
        futureBookingForItem2.setBooker(user2);
        futureBookingForItem2.setStatus(Status.APPROVED);

        Thread.sleep(50);

        waitingBookingForItem1 = new Booking();
        waitingBookingForItem1.setStart(LocalDateTime.now().plusHours(1));
        waitingBookingForItem1.setEnd(LocalDateTime.now().plusHours(2));
        waitingBookingForItem1.setItem(item1);
        waitingBookingForItem1.setBooker(user2);
        waitingBookingForItem1.setStatus(Status.WAITING);

        Thread.sleep(50);

        waitingBookingForItem2 = new Booking();
        waitingBookingForItem2.setStart(LocalDateTime.now().plusHours(1));
        waitingBookingForItem2.setEnd(LocalDateTime.now().plusHours(2));
        waitingBookingForItem2.setItem(item2);
        waitingBookingForItem2.setBooker(user2);
        waitingBookingForItem2.setStatus(Status.WAITING);

        Thread.sleep(50);

        rejectedBookingForItem1 = new Booking();
        rejectedBookingForItem1.setStart(LocalDateTime.now().plusHours(1));
        rejectedBookingForItem1.setEnd(LocalDateTime.now().plusHours(2));
        rejectedBookingForItem1.setItem(item1);
        rejectedBookingForItem1.setBooker(user2);
        rejectedBookingForItem1.setStatus(Status.REJECTED);

        Thread.sleep(50);

        rejectedBookingForItem2 = new Booking();
        rejectedBookingForItem2.setStart(LocalDateTime.now().plusHours(1));
        rejectedBookingForItem2.setEnd(LocalDateTime.now().plusHours(2));
        rejectedBookingForItem2.setItem(item2);
        rejectedBookingForItem2.setBooker(user2);
        rejectedBookingForItem2.setStatus(Status.REJECTED);
    }

    @SneakyThrows
    private void addBookingsInDb() {
        bookingRepository.save(currentBookingForItem1);
        bookingRepository.save(currentBookingForItem2);
        bookingRepository.save(pastBookingForItem1);
        bookingRepository.save(pastBookingForItem2);
        bookingRepository.save(futureBookingForItem1);
        bookingRepository.save(futureBookingForItem2);
        bookingRepository.save(waitingBookingForItem1);
        bookingRepository.save(waitingBookingForItem2);
        bookingRepository.save(rejectedBookingForItem1);
        bookingRepository.save(rejectedBookingForItem2);
    }
}

