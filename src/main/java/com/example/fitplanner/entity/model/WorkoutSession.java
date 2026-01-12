package com.example.fitplanner.entity.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = {"program"})
@NoArgsConstructor
public class WorkoutSession extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @Column(nullable = false)
    private LocalDate scheduledFor;

    @OneToMany(mappedBy = "workoutSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExerciseProgress> exercises = new HashSet<>();

    public WorkoutSession(Program program, LocalDate scheduledFor) {
        this.program = program;
        this.scheduledFor = scheduledFor;
    }

    public void addExercise(ExerciseProgress progress) {
        exercises.add(progress);
    }
}