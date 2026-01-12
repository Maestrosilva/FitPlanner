package com.example.fitplanner.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class StatsUserDto implements Serializable {
    private List<WeightEntryDto> weightChanges = new ArrayList<>();
    private List<StatsExerciseDto> progresses = new ArrayList<>();
    private String measuringUnits = "kg";
}