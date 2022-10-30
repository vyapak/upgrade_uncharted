package com.upgrade.uncharted.service;

import com.upgrade.uncharted.dto.BookingResponse;
import com.upgrade.uncharted.dto.UpdateBookingRequest;
import com.upgrade.uncharted.entity.Booking;
import com.upgrade.uncharted.repository.BookingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingServiceImplIntegTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @SpyBean()
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void shouldUpdateBooking_withoutConcurrency() {
        final Booking booking = bookingRepository.save(Booking.builder()
                .bookingIdentifier(UUID.randomUUID().toString())
                .build());
        Assertions.assertEquals(0, booking.getVersion());

        UpdateBookingRequest request = UpdateBookingRequest.builder().arrivalDate("2022-10-05").departureDate("2022-10-07").build();
        final List<UpdateBookingRequest> requests = List.of(request, request);

        for (UpdateBookingRequest req : requests) {
            BookingResponse response = bookingService.updateBooking(booking.getBookingIdentifier(), request);
        }

        final Booking savedBooking = bookingRepository.findByBookingIdentifier(booking.getBookingIdentifier()).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, savedBooking.getVersion()),
                () -> Assertions.assertEquals(LocalDate.parse("2022-10-05"), savedBooking.getStartDate()),
                () -> Assertions.assertEquals(LocalDate.parse("2022-10-07") ,savedBooking.getEndDate()),
                () -> verify(bookingRepository, times(2)).updateBooking(anyString(), any(), any())
        );
    }

    @Test
    public void shouldUpdateBooking_withOptimisticLockingHandling() throws InterruptedException {
        final Booking booking = bookingRepository.save(Booking.builder()
                .bookingIdentifier(UUID.randomUUID().toString())
                .build());
        Assertions.assertEquals(0, booking.getVersion());

        final ExecutorService executor = Executors.newFixedThreadPool(2);

        UpdateBookingRequest request = UpdateBookingRequest.builder().arrivalDate("2022-10-05").departureDate("2022-10-07").build();
        final List<UpdateBookingRequest> requests = List.of(request, request);

        for (UpdateBookingRequest req : requests) {
            executor.execute(() -> bookingService.updateBooking(booking.getBookingIdentifier(), req));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        final Booking updatedBooking = bookingRepository.findByBookingIdentifier(booking.getBookingIdentifier()).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, updatedBooking.getVersion()),
                () -> Assertions.assertEquals(LocalDate.parse("2022-10-05"), updatedBooking.getStartDate()),
                () -> Assertions.assertEquals(LocalDate.parse("2022-10-07") ,updatedBooking.getEndDate()),
                () -> verify(bookingRepository, times(3)).updateBooking(anyString(), any(), any())
        );
    }


}
