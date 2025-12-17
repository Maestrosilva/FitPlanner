package com.example.fitplanner.entity.model;

import com.example.fitplanner.entity.enums.Difficulty;
import com.example.fitplanner.entity.enums.Gender;
import com.example.fitplanner.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
public class User extends BaseEntity {

    @NonNull
    @NotBlank
    @Size(min = 2, max = 24)
    private String firstName;

    @NonNull
    @NotBlank
    @Size(min = 2, max = 24)
    private String lastName;

    @NonNull
    @NotBlank
    @Size(max = 64)
    private String username;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @NonNull
    @Min(10)
    @Max(120)
    private Integer age;

    @NonNull
    @Min(20)
    @Max(300)
    private Double weight;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty experience;

    @NonNull
    @Email
    private String email;

    @NonNull
    @NotBlank
    @Size(min = 4)
    private String password;

    private LocalDate createdAt = LocalDate.now();

    private LocalDate lastUpdated = LocalDate.now();

    @OneToMany(mappedBy = "user")
    private Set<Program> programs = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ExerciseProgress> completedExercises = new HashSet<>();

    @Column
    private String profileImageUrl;

    @Column(nullable = false)
    private String theme = "dark";

    @Column(nullable = false)
    private String language = "en";

    @Column(nullable = false)
    private String measuringUnits = "kg";

    public void updatePreferences(String theme, String language, String units) {
        this.theme = theme;
        this.language = language;
        this.measuringUnits = units;
    }
}