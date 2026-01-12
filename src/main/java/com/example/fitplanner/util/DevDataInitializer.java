package com.example.fitplanner.util;

import com.example.fitplanner.entity.enums.Difficulty;
import com.example.fitplanner.entity.enums.Gender;
import com.example.fitplanner.entity.enums.Role;
import com.example.fitplanner.entity.model.*;
import com.example.fitplanner.repository.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")
public class DevDataInitializer {

    private final UserRepository userRepository;
    private final ProgramRepository programRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ExerciseProgressRepository exerciseProgressRepository;
    private final SHA256Hasher hasher;

    public DevDataInitializer(UserRepository userRepository,
                              ProgramRepository programRepository,
                              ExerciseRepository exerciseRepository,
                              WorkoutSessionRepository workoutSessionRepository,
                              ExerciseProgressRepository exerciseProgressRepository,
                              SHA256Hasher hasher) {
        this.userRepository = userRepository;
        this.programRepository = programRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.exerciseProgressRepository = exerciseProgressRepository;
        this.hasher = hasher;
    }

    @PostConstruct
    public void init() {
        User user = initDemoUser();
        Program program = initProgram(user);
        List<Exercise> exercises = initExercises();
        initProgress(program, user, exercises);
    }

    // --------------------------------------------------------

    private User initDemoUser() {
        return userRepository.findByUsername("testuser")
                .orElseGet(() -> {
                    User user = new User();
                    user.setUsername("testuser");
                    user.setEmail("testuser@test.com");
                    user.setRole(Role.CLIENT);
                    user.setLanguage("en");
                    user.setPassword(hasher.hash("test123"));

                    user.setFirstName("Test");
                    user.setLastName("User");
                    user.setGender(Gender.MALE);
                    user.setAge(25);
                    user.setWeight(70.0);
                    user.setExperience(Difficulty.BEGINNER);

                    return userRepository.save(user);
                });
    }



    private Program initProgram(User user) {
        return programRepository.getByUserId(user.getId()).stream().findFirst()
                .orElseGet(() -> {
                    Program program = new Program();
                    program.setUser(user);
                    program.setName("Demo Program");
                    return programRepository.save(program);
                });
    }

    private List<Exercise> initExercises() {
        if (exerciseRepository.count() >= 60) {
            return exerciseRepository.findAll();
        }

        for (int i = 1; i <= 60; i++) {
            Exercise exercise = new Exercise();
            exercise.setName("Exercise" + i);
            exerciseRepository.save(exercise);
        }
        return exerciseRepository.findAll();
    }

    private void initProgress(Program program, User user, List<Exercise> exercises) {
        if (!exerciseProgressRepository.findByUserId(user.getId()).isEmpty()) {
            return;
        }

        Random random = new Random();
        LocalDate startDate = LocalDate.now().minusWeeks(12);

        for (Exercise exercise : exercises) {

            for (int week = 0; week < 8; week++) {

                WorkoutSession session = new WorkoutSession(
                        program,
                        startDate.plusWeeks(week)
                );
                workoutSessionRepository.save(session);

                ExerciseProgress progress = new ExerciseProgress(
                        session,
                        exercise,
                        user,
                        8 + random.nextInt(6),
                        3 + random.nextInt(3),
                        20.0 + random.nextInt(50),
                        session.getScheduledFor()
                );

                progress.markCompleted(session.getScheduledFor());
                exerciseProgressRepository.save(progress);
            }
        }
    }
}