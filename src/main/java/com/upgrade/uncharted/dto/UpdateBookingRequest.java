package com.upgrade.uncharted.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingRequest {
    private String arrivalDate;
    private String departureDate;
}
