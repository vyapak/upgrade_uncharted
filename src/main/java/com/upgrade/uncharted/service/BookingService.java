package com.upgrade.uncharted.service;

import com.upgrade.uncharted.dto.BookingRequest;
import com.upgrade.uncharted.dto.BookingResponse;
import com.upgrade.uncharted.dto.UpdateBookingRequest;

import java.time.LocalDate;
import java.util.Set;

public interface BookingService {
    int deleteBooking(final String bookingIdentifier);
    Set<LocalDate> getAvailabilities(final LocalDate fromDate, final LocalDate toDate);
    BookingResponse book(final BookingRequest bookingRequest);
    BookingResponse updateBooking(final String bookingIdentifier, final UpdateBookingRequest updateBookingRequest);
}
