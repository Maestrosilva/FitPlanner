package com.example.fitplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsExerciseDto implements Serializable, WeightAware {
    private Long exerciseId;
    private String exerciseName;
    private LocalDate completedDate;
    private Integer completedExercisesInARow;
    private Integer reps;
    private Integer sets;
    private Double weight;
}