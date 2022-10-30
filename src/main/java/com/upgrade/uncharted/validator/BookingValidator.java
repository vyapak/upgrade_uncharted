package com.upgrade.uncharted.validator;


import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingValidator {
    public static void validateBookingPeriod(String from, String to) {
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        long daysToBook = ChronoUnit.DAYS.between(fromDate, toDate);


        Validate.isTrue(daysToBook > 0 && daysToBook <= 3,
                "The campsite can be reserved for minimum 1 day and maximum 3 days.");
    }

    public static void validateDateOfArrival(String from) {
        LocalDate fromDate = LocalDate.parse(from);

        long daysFromNow = ChronoUnit.DAYS.between(LocalDate.now(), fromDate);

        Validate.isTrue(daysFromNow >= 1, "The campsite can be reserved minumum 1 day ahead of arrivial.");
    }
}
