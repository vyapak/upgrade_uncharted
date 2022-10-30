package com.upgrade.uncharted.service;

import com.upgrade.uncharted.dto.BookingRequest;
import com.upgrade.uncharted.dto.BookingResponse;
import com.upgrade.uncharted.dto.UpdateBookingRequest;
import com.upgrade.uncharted.entity.Booking;
import com.upgrade.uncharted.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    @Transactional
    public int deleteBooking(String bookingIdentifier) {
        return bookingRepository.deleteByBookingIdentifier(bookingIdentifier);
    }

    @Override
    public Set<LocalDate> getAvailabilities(LocalDate fromDate, LocalDate toDate) {
        Set<Booking> existingBookings = bookingRepository.findAllByStartDateGreaterThan(LocalDate.now());
        Set<LocalDate> askedAvailability = fromDate.datesUntil(toDate.plusDays(1))
                .collect(Collectors.toSet());
        Set<LocalDate> unavailableDates = new HashSet<>();

        for (Booking booking : existingBookings) {
            Set<LocalDate> bookedDates = booking.getStartDate().datesUntil(booking.getEndDate().plusDays(1))
                    .collect(Collectors.toSet());
            unavailableDates.addAll(bookedDates);
        }

        return askedAvailability
                .stream()
                .filter(day -> !unavailableDates.contains(day))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public BookingResponse book(BookingRequest bookingRequest) {
        Set<Booking> existingBookings = bookingRepository.findAllByStartDateGreaterThan(LocalDate.now());

        LocalDate bookingFromDate = LocalDate.parse(bookingRequest.getArrivalDate());
        LocalDate bookingToDate = LocalDate.parse(bookingRequest.getDepartureDate());

        boolean overlaps = existingBookings
                .stream()
                .anyMatch(booking -> booking.getStartDate().isBefore(bookingToDate)
                        && bookingFromDate.isBefore(booking.getEndDate()));

        if (!overlaps) {
            Booking booking = bookingRepository.save(Booking.builder()
                    .bookingIdentifier(UUID.randomUUID().toString())
                    .startDate(bookingFromDate)
                    .endDate(bookingToDate)
                    .build());
            return new BookingResponse(booking.getBookingIdentifier());
        }

        throw new IllegalArgumentException("The selected period is already booked.");
    }

    @Override
    @Transactional
    public BookingResponse updateBooking(String bookingIdentifier, UpdateBookingRequest updateBookingRequest) {
        Booking booking = bookingRepository.findByBookingIdentifier(bookingIdentifier).get();
        Set<Booking> existingBookings = bookingRepository.findAllByStartDateGreaterThan(LocalDate.now());

        existingBookings.remove(booking);

        LocalDate bookingFromDate = LocalDate.parse(updateBookingRequest.getArrivalDate());
        LocalDate bookingToDate = LocalDate.parse(updateBookingRequest.getDepartureDate());
        booking.setStartDate(bookingFromDate);
        booking.setEndDate(bookingToDate);


        boolean overlaps = existingBookings
                .stream()
                .anyMatch(b -> b.getStartDate().isBefore(booking.getEndDate())
                        && booking.getStartDate().isBefore(b.getEndDate()));

        if (!overlaps) {
            try {
                bookingRepository.updateBooking(bookingIdentifier, bookingFromDate, bookingToDate);
            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("Somebody has already updated the booking with this identifier:{} in concurrent transaction. Will try again...", bookingIdentifier);
                bookingRepository.updateBooking(bookingIdentifier, bookingFromDate, bookingToDate);
            }

            return new BookingResponse(booking.getBookingIdentifier());
        }

        throw new IllegalArgumentException("The selected period is already booked.");
    }
}
