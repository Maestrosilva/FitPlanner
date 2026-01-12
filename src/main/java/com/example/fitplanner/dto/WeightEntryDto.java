package com.example.fitplanner.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class WeightEntryDto implements Serializable, WeightAware {
    private LocalDate date;
    private Double weight;

    public WeightEntryDto(LocalDate date, Double weight) {
        this.date = date;
        this.weight = weight;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getWeight() {
        return weight;
    }
}

