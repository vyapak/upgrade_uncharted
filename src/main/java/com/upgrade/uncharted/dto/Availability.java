package com.upgrade.uncharted.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
public class Availability {
    private LocalDate from;
    private LocalDate to;
}
