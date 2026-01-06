package com.example.fitplanner.config;

import com.example.fitplanner.dto.CreatedProgramDto;
import com.example.fitplanner.dto.DayWorkout;
import com.example.fitplanner.dto.ExerciseProgressDto;
import com.example.fitplanner.entity.model.ExerciseProgress;
import com.example.fitplanner.entity.model.Program;
import com.example.fitplanner.entity.model.WorkoutSession;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    private final SessionCleanupInterceptor sessionCleanupInterceptor;

    public AppConfig(SessionCleanupInterceptor sessionCleanupInterceptor) {
        this.sessionCleanupInterceptor = sessionCleanupInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionCleanupInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/img/**", "/favicon.ico", "/webjars/**", "/error");
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        mapper.typeMap(ExerciseProgress.class, ExerciseProgressDto.class).addMappings(m -> {
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
                        .filter(d -> d.getScheduledFor().isAfter(startOfWeek) && d.getScheduledFor().isBefore(endOfWeek))
                        .forEach(session -> {
                            String dayName = session.getScheduledFor().getDayOfWeek().name();
                            List<ExerciseProgressDto> dtos = session.getExercises().stream()
                                    .map(ex -> mapper.map(ex, ExerciseProgressDto.class))
                                    .collect(Collectors.toList());
                            fullWeek.get(dayName).addAll(dtos);
                });
            }
            return fullWeek.entrySet().stream()
                    .map(entry -> new DayWorkout(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        };

        mapper.typeMap(Program.class, CreatedProgramDto.class).addMappings(m -> {
            m.using(sessionsToDaysConverter).map(Program::getSessions, CreatedProgramDto::setWeekDays);
        });

        return mapper;
    }
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        resolver.setCookieName("fitplannerLanguage");
        resolver.setCookieMaxAge(60 * 60 * 24);
        return resolver;
    }

    @Bean
    public ServletContextInitializer initializer() {
        return servletContext -> {
            servletContext.setSessionTimeout(30);
        };
    }
}