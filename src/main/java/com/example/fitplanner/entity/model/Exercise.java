package com.example.fitplanner.entity.model;

import com.example.fitplanner.entity.enums.Category;
import com.example.fitplanner.entity.enums.ExerciseType;
import com.example.fitplanner.entity.enums.EquipmentType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Exercise extends BaseEntity{
    @NonNull
    @NotBlank
    @Size(min = 3, max = 32)
    private String name;

    @Size(max = 500)
    private String description;

    @NonNull
    @Enumerated(value = EnumType.STRING)
    private Category category;

    @NonNull
    @Enumerated(value = EnumType.STRING)
    private ExerciseType exerciseType;

    @NonNull
    @Enumerated(value = EnumType.STRING)
    private EquipmentType equipmentType;

    @NonNull
    @NotBlank
//    @Size(max = 32)
    private String imageUrl;

    @NonNull
    @NotBlank
//    @Size(max = 32)
    private String videoUrl;
}