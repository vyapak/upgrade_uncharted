package com.upgrade.uncharted.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String arrivalDate;
    private String departureDate;
}
