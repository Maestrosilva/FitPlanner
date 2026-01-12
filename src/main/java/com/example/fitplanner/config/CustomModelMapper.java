package com.example.fitplanner.config;

import com.example.fitplanner.dto.*;
import com.example.fitplanner.entity.model.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomModelMapper extends ModelMapper {

    private static final double KG_TO_LB = 2.20462262;

    public CustomModelMapper() {
        super();
        configureMapper();
    }

    private void configureMapper() {
        this.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        this.typeMap(ExerciseProgress.class, ExerciseProgressDto.class).addMappings(m -> {
            m.skip(ExerciseProgressDto::setExerciseId);
            m.map(src -> src.getExercise().getName(), ExerciseProgressDto::setName);
            m.map(src -> src.getExercise().getId(), ExerciseProgressDto::setExerciseId);
            m.map(src -> src.getWorkoutSession().getProgram().getName(), ExerciseProgressDto::setProgramName);
        });

        Converter<Set<WorkoutSession>, List<DayWorkout>> sessionsToDaysConverter = context -> {
            Map<String, List<ExerciseProgressDto>> fullWeek = new LinkedHashMap<>();
            for (DayOfWeek day : DayOfWeek.values()) {
                fullWeek.put(day.name(), new ArrayList<>());
            }

            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = startOfWeek.plusDays(7);

            if (context.getSource() != null) {
                context.getSource().stream()
                        .filter(d -> d.getScheduledFor() != null &&
                                d.getScheduledFor().isAfter(startOfWeek.minusDays(1)) &&
                                d.getScheduledFor().isBefore(endOfWeek))
                        .forEach(session -> {
                            String dayName = session.getScheduledFor().getDayOfWeek().name();
                            List<ExerciseProgressDto> dtos = session.getExercises().stream()
                                    .map(ex -> this.map(ex, ExerciseProgressDto.class))
                                    .collect(Collectors.toList());
                            fullWeek.get(dayName).addAll(dtos);
                        });
            }

            return fullWeek.entrySet().stream()
                    .map(entry -> new DayWorkout(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        };

        this.typeMap(User.class, StatsUserDto.class).setPostConverter(context -> {
            User source = context.getSource();
            StatsUserDto destination = context.getDestination();

            if (source == null || destination == null) return destination;

            boolean isLbs = "lbs".equalsIgnoreCase(source.getMeasuringUnits());
            
            if (source.getCompletedExercises() != null) {
                List<StatsExerciseDto> convertedProgress = getStatsExerciseDtos(source, isLbs, KG_TO_LB);
                destination.setProgresses(convertedProgress);
            }

            if (source.getWeightChanges() != null) {
                List<WeightEntryDto> weightDtos = new ArrayList<>();

                for (WeightEntry entry : source.getWeightChanges()) {
                    if (entry == null) continue;

                    WeightEntryDto dto = new WeightEntryDto();
                    Double weightVal = entry.getWeight();

                    if (isLbs && weightVal != null) {
                        weightVal = weightVal * KG_TO_LB;
                        weightVal = Math.round(weightVal * 100.0) / 100.0;
                    }

                    dto.setWeight(weightVal);
                    dto.setDate(entry.getDate());

                    weightDtos.add(dto);
                }

                destination.setWeightChanges(weightDtos);
            }

            destination.setMeasuringUnits(source.getMeasuringUnits());

            return destination;
        });
    }

    private List<StatsExerciseDto> getStatsExerciseDtos(User source, boolean isLbs, double KG_TO_LB) {
        List<StatsExerciseDto> convertedProgress = new ArrayList<>();

        for (ExerciseProgress entity : source.getCompletedExercises()) {
            try {
                StatsExerciseDto dto = new StatsExerciseDto();
                dto.setExerciseId(entity.getExercise().getId());
                dto.setWeight(entity.getWeight());
                dto.setCompletedExercisesInARow(entity.getCompletedExercisesInARow());

                LocalDate date = (entity.getLastCompleted() != null) ?
                        entity.getLastCompleted() : entity.getLastScheduled();
                dto.setCompletedDate(date);

                if (isLbs && dto.getWeight() != null) {
                    double lbVal = dto.getWeight() * KG_TO_LB;
                    dto.setWeight(Math.round(lbVal * 100.0) / 100.0);
                }

                if (dto.getCompletedDate() != null) {
                    convertedProgress.add(dto);
                }
            } catch (Exception e) {}
        }
        return convertedProgress;
    }
}