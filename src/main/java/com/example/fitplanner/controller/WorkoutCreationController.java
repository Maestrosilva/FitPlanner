package com.example.fitplanner.controller;

import com.example.fitplanner.dto.DayWorkout;
import com.example.fitplanner.dto.ExerciseDto;
import com.example.fitplanner.dto.ExerciseProgressDto;
import com.example.fitplanner.dto.UserDto;
import com.example.fitplanner.entity.model.Exercise;
import com.example.fitplanner.entity.model.Program;
import com.example.fitplanner.entity.model.WorkoutSession;
import com.example.fitplanner.entity.model.ExerciseProgress;
import com.example.fitplanner.service.ExerciseService;
import com.example.fitplanner.service.ProgramService;
import com.example.fitplanner.service.ExerciseProgressService;
import com.example.fitplanner.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class WorkoutCreationController {

    private final UserService userService;
    private final ProgramService programService;
    private final ExerciseProgressService exerciseProgressService;
    private final ExerciseService exerciseService;

    @Autowired
    public WorkoutCreationController(UserService userService, ProgramService programService,
                                     ExerciseProgressService exerciseProgressService, ExerciseService exerciseService) {
        this.userService = userService;
        this.programService = programService;
        this.exerciseProgressService = exerciseProgressService;
        this.exerciseService = exerciseService;
    }

    @GetMapping("/create/{id}")
    public String createWorkout(@PathVariable Long id,
                                HttpSession session,
                                Model model) {
        UserDto sessionUser = (UserDto) session.getAttribute("loggedUser");
        if (sessionUser == null || !sessionUser.getId().equals(id)) {
            return "redirect:/login";
        }
        session.setAttribute("loggedUser", sessionUser);
        model.addAttribute("theme", session.getAttribute("theme"));
        model.addAttribute("language", session.getAttribute("language"));
        model.addAttribute("units", session.getAttribute("units"));
        model.addAttribute("userDto", sessionUser);
        List<DayWorkout> weekDays =
                (List<DayWorkout>) session.getAttribute("weekDays");
        if (weekDays == null) {
            weekDays = new ArrayList<>();
            LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
            for (int i = 0; i < 7; i++) {
                LocalDate date = startOfWeek.plusDays(i);
                String dayKey = date.getDayOfWeek().name().toLowerCase();
                weekDays.add(new DayWorkout(dayKey, new ArrayList<>()));
                System.out.println(weekDays.stream().map(DayWorkout::toString).collect(Collectors.joining(", ")));
            }
            session.setAttribute("weekDays", weekDays);
        }
        model.addAttribute("weekDays", weekDays);
        return "create";
    }

    @PostMapping("/add-exercise/{day}/{id}")
    public String addExerciseToDay(@PathVariable String day,
                                   @PathVariable Long id,
                                   HttpSession session,
                                   Model model) {
        List<DayWorkout> weekDays = (List<DayWorkout>) session.getAttribute("weekDays");
        System.out.println(weekDays.stream().map(DayWorkout::toString).collect(Collectors.joining(", ")));
        if (weekDays == null) return "redirect:/create/" + id;
        session.setAttribute("currentDay", day);
        model.addAttribute("theme", session.getAttribute("theme"));
        model.addAttribute("language", session.getAttribute("language"));
        model.addAttribute("units", session.getAttribute("units"));
        return "redirect:/exercise-log";
    }

    @PostMapping("/edit-exercise/{day}/{exerciseId}/{id}")
    public String editExercise(@PathVariable String day,
                               @PathVariable Long exerciseId,
                               @PathVariable Long id,
                               HttpSession session,
                               Model model) {
        session.setAttribute("currentDay", day);
        session.setAttribute("currentExerciseId", exerciseId);
        model.addAttribute("theme", session.getAttribute("theme"));
        model.addAttribute("language", session.getAttribute("language"));
        model.addAttribute("units", session.getAttribute("units"));
        return "redirect:/exercise-log/edit";
    }

    @GetMapping("/exercise-log")
    public String showExerciseLog(HttpSession session, Model model){
        UserDto userDto = (UserDto) session.getAttribute("loggedUser");
        Set<ExerciseDto> exercises = exerciseService.getAll();
        model.addAttribute("exercises", exercises);
        model.addAttribute("userDto", userDto);
        model.addAttribute("theme", session.getAttribute("theme"));
        model.addAttribute("language", session.getAttribute("language"));
        model.addAttribute("units", session.getAttribute("units"));
        return "exercises-log";
    }
}