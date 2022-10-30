package com.upgrade.uncharted.service;

import com.upgrade.uncharted.dto.BookingRequest;
import com.upgrade.uncharted.dto.BookingResponse;
import com.upgrade.uncharted.dto.UpdateBookingRequest;
import com.upgrade.uncharted.entity.Booking;
import com.upgrade.uncharted.repository.BookingRepository;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldDeleteBooking_whenDeleteBookingMethodIsCalled() {
        bookingService.deleteBooking(UUID.randomUUID().toString());

        Mockito.verify(bookingRepository, Mockito.times(1)).deleteByBookingIdentifier(anyString());
    }

    @Test
    public void shouldReturnAvailabilities_whenGetAvailabilitiesIsCalled() {
        Mockito.when(bookingRepository.findAllByStartDateGreaterThan(any(LocalDate.class))).thenReturn(getBookingFromDatabase());

        Set<LocalDate> availabilities = bookingService.getAvailabilities(LocalDate.of(2022, 10, 1), LocalDate.of(2022, 10, 31));

        Assertions.assertEquals(availabilities.size(), 22);
    }

    @Test
    public void shouldReturnUniqueId_whenBookIsCalledAndSuccecsfull() {
        Mockito.when(bookingRepository.findAllByStartDateGreaterThan(any())).thenReturn(getBookingFromDatabase());
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(
                new Booking(1,
                        "aaaa",
                        LocalDate.of(2022, 10, 05),
                        LocalDate.of(2022, 10, 07),
                        1));

        BookingRequest request = BookingRequest.builder().arrivalDate("2022-10-05").departureDate("2022-10-07").build();
        BookingResponse response = bookingService.book(request);

        Assertions.assertEquals(response.getBookingIdentifier(), "aaaa");
    }

    @Test
    public void shouldThrowException_whenBookIsCalledAndPeriodIsOverlapping() {
        Mockito.when(bookingRepository.findAllByStartDateGreaterThan(any())).thenReturn(getBookingFromDatabase());

        BookingRequest request = BookingRequest.builder().arrivalDate("2022-10-01").departureDate("2022-10-03").build();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BookingResponse response = bookingService.book(request);
        });


        Assertions.assertEquals("The selected period is already booked.", exception.getMessage());
    }

    @Test
    public void shouldThrowException_whenUpdateBookingIsCalledAndPeriodIsOverlapping() {
        Set<Booking> existingBooking = getBookingFromDatabase();
        Mockito.when(bookingRepository.findByBookingIdentifier(anyString())).thenReturn(existingBooking.stream().findFirst());
        Mockito.when(bookingRepository.findAllByStartDateGreaterThan(any())).thenReturn(existingBooking);

        UpdateBookingRequest request = UpdateBookingRequest.builder().arrivalDate("2022-10-01").departureDate("2022-10-13").build();
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BookingResponse response = bookingService.updateBooking("aaaa", request);
        });


        Assertions.assertEquals("The selected period is already booked.", exception.getMessage());
    }

    @Test
    public void shouldReturnSameUniqueId_whenUpdateBookingIsCalledAndSuccecsfull() {
        Set<Booking> existingBooking = getBookingFromDatabase();
        Mockito.when(bookingRepository.findByBookingIdentifier(anyString())).thenReturn(existingBooking.stream().findFirst());
        Mockito.when(bookingRepository.findAllByStartDateGreaterThan(any())).thenReturn(existingBooking);
        Mockito.when(bookingRepository.updateBooking(anyString(), any(LocalDate.class), any(LocalDate.class))).thenReturn(
                1);

        UpdateBookingRequest request = UpdateBookingRequest.builder().arrivalDate("2022-10-05").departureDate("2022-10-07").build();
        BookingResponse response = bookingService.updateBooking("aaaa", request);

        Assertions.assertEquals(response.getBookingIdentifier(), "aaaa");
    }

    private Set<Booking> getBookingFromDatabase() {
        Booking booking1 = new Booking(
                1,
                "aaaa",
                LocalDate.of(2022, 10, 1),
                LocalDate.of(2022, 10, 3),
                1);
        Booking booking2 = new Booking(
                2,
                "bbbb",
                LocalDate.of(2022, 10, 10),
                LocalDate.of(2022, 10, 13),
                1);
        Booking booking3 = new Booking(
                3,
                "cccc",
                LocalDate.of(2022, 10, 20),
                LocalDate.of(2022, 10, 21),
                1);

        return Sets.set(booking1, booking2, booking3);
    }
}
