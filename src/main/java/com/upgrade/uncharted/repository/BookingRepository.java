package com.upgrade.uncharted.repository;

import com.upgrade.uncharted.entity.Booking;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends CrudRepository<Booking, Long> {

    Set<Booking> findAllByStartDateGreaterThan(final LocalDate startDate);
    Optional<Booking> findByBookingIdentifier(final String bookingIdentifier);
    int deleteByBookingIdentifier(final String bookingIdentifier);

    @Modifying
    @Transactional
    @Query("update Booking b set b.startDate = :fromDate, b.endDate = :toDate where b.bookingIdentifier = :bookingIdentifier")
    int updateBooking(@Param("bookingIdentifier") final String identifier, @Param("fromDate") final LocalDate fromDate,
                      @Param("toDate") final LocalDate toDate);
}
