package com.example.fitplanner.config;

import com.example.fitplanner.dto.*;
import com.example.fitplanner.entity.model.ExerciseProgress;
import com.example.fitplanner.entity.model.Program;
import com.example.fitplanner.entity.model.User;
import com.example.fitplanner.entity.model.WorkoutSession;
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

        // --- ExerciseProgress to ExerciseProgressDto ---
        this.typeMap(ExerciseProgress.class, ExerciseProgressDto.class).addMappings(m -> {
            m.skip(ExerciseProgressDto::setExerciseId);
            m.map(src -> src.getExercise().getName(), ExerciseProgressDto::setName);
            m.map(src -> src.getExercise().getId(), ExerciseProgressDto::setExerciseId);
            m.map(src -> src.getWorkoutSession().getProgram().getName(), ExerciseProgressDto::setProgramName);
        });

        // --- Program to CreatedProgramDto (Weekly View Logic) ---
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
            double KG_TO_LB = 2.20462262;

            // IMPORTANT: Check if the collection is initialized
            if (source.getCompletedExercises() != null) {
                List<StatsExerciseDto> convertedProgress = new ArrayList<>();

                for (ExerciseProgress entity : source.getCompletedExercises()) {
                    try {
                        // Manually map fields if the internal 'this.map' is causing recursion/crashes
                        StatsExerciseDto dto = new StatsExerciseDto();
                        dto.setExerciseId(entity.getExercise().getId());
                        dto.setWeight(entity.getWeight());
                        dto.setCompletedExercisesInARow(entity.getCompletedExercisesInARow());

                        // Safety check for dates
                        LocalDate date = (entity.getLastCompleted() != null) ?
                                entity.getLastCompleted() : entity.getLastScheduled();
                        dto.setCompletedDate(date);

                        // Conversion
                        if (isLbs && dto.getWeight() != null) {
                            double lbVal = dto.getWeight() * KG_TO_LB;
                            dto.setWeight(Math.round(lbVal * 100.0) / 100.0);
                        }

                        if (dto.getCompletedDate() != null) {
                            convertedProgress.add(dto);
                        }
                    } catch (Exception e) {
                        // Skip this specific entry if it's broken (e.g. null exercise)
                        continue;
                    }
                }
                destination.setProgresses(convertedProgress);
            }

            destination.setMeasuringUnits(source.getMeasuringUnits());
            return destination;
        });
    }
}