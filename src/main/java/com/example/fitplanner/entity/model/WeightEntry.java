package com.example.fitplanner.entity.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Embeddable
@Data
@AllArgsConstructor
public class WeightEntry {
    private Double weight;
    private LocalDate date;

    public WeightEntry() {

    }
}
