package com.upgrade.uncharted.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class BookingValidatorTest {

    @Test()
    public void shouldThrowAnException_whenCampsiteIsNotReserved1DayAheadOfArrival() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BookingValidator.validateDateOfArrival(LocalDate.now().toString());
        });

        Assertions.assertEquals("The campsite can be reserved minumum 1 day ahead of arrivial.", exception.getMessage());
    }

    @Test()
    public void shouldNotThrowAnException_whenCampsiteIsReserved1DayAheadOfArrival() {
        Assertions.assertDoesNotThrow(() -> {
           BookingValidator.validateDateOfArrival(LocalDate.now().plusDays(1).toString());
        });
    }

    @Test()
    public void shouldThrowAnException_whenCampsiteIsNotReservedForMin1Day() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BookingValidator.validateBookingPeriod(LocalDate.now().toString(), LocalDate.now().toString());
        });

        Assertions.assertEquals("The campsite can be reserved for minimum 1 day and maximum 3 days.", exception.getMessage());
    }

    @Test()
    public void shouldThrowAnException_whenCampsiteIsNotReservedForMax3Day() {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            BookingValidator.validateBookingPeriod(LocalDate.now().toString(), LocalDate.now().plusDays(5).toString());
        });

        Assertions.assertEquals("The campsite can be reserved for minimum 1 day and maximum 3 days.", exception.getMessage());
    }

    @Test()
    public void shouldNotThrowAnException_whenCampsiteIsReservedBetween1And3Days() {
        Assertions.assertDoesNotThrow(() -> {
            BookingValidator.validateBookingPeriod(LocalDate.now().toString(), LocalDate.now().plusDays(2).toString());
        });
    }
}
