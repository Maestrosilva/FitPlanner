package com.example.fitplanner.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//@Component
@Data
@NoArgsConstructor
public class DateWorkout implements Serializable {
    private LocalDate date;
    private List<ExerciseProgressDto> exercises;

    private String programName;

    public DateWorkout(LocalDate date, List<ExerciseProgressDto> exercises) {
        this.date = date;
        this.exercises = exercises != null ? exercises : new ArrayList<>();
    }
}
