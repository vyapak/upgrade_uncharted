package com.upgrade.uncharted.controller;

import com.upgrade.uncharted.dto.BookingRequest;
import com.upgrade.uncharted.dto.BookingResponse;
import com.upgrade.uncharted.dto.UpdateBookingRequest;
import com.upgrade.uncharted.service.BookingServiceImpl;
import com.upgrade.uncharted.validator.BookingValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private final static int DEFAULT_AVAILABILITY_RANGE = 1;
    @Autowired
    private BookingServiceImpl bookingServiceImpl;

    @GetMapping("/availabilities")
    public ResponseEntity<Set<LocalDate>> getAvailabilities(@Valid @NotNull @RequestParam("from") final String from,
                                                            @RequestParam("to") final String to) {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = fromDate.plusMonths(DEFAULT_AVAILABILITY_RANGE);

        if (to != null) {
            toDate = LocalDate.parse(to);
        }

        return ResponseEntity.ok(bookingServiceImpl.getAvailabilities(fromDate, toDate));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingResponse> book(@RequestBody final BookingRequest bookingRequest) {
        BookingValidator.validateBookingPeriod(bookingRequest.getArrivalDate(), bookingRequest.getDepartureDate());
        BookingValidator.validateDateOfArrival(bookingRequest.getArrivalDate());

        return ResponseEntity.status(HttpStatus.CREATED).body(bookingServiceImpl.book(bookingRequest));
    }

    @PutMapping("/{bookingIdentifier}")
    public ResponseEntity<BookingResponse> updateBooking(@Valid @NotNull @PathVariable("bookingIdentifier") final String bookingIdentifier,
                                                         @RequestBody final UpdateBookingRequest updateBookingRequest) {
        BookingValidator.validateBookingPeriod(updateBookingRequest.getArrivalDate(), updateBookingRequest.getDepartureDate());
        BookingValidator.validateDateOfArrival(updateBookingRequest.getArrivalDate());

        return ok(bookingServiceImpl.updateBooking(bookingIdentifier, updateBookingRequest));
    }

    @DeleteMapping("/{bookingIdentifier}")
    public ResponseEntity<String> deleteBooking(@PathVariable("bookingIdentifier") final String bookingIdentifier) {
        if (bookingServiceImpl.deleteBooking(bookingIdentifier) != 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No booking were found with that booking identifier.");
    }
}
