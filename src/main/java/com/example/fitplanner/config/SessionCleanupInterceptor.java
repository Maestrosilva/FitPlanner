package com.example.fitplanner.config;

import com.example.fitplanner.service.SessionModelService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class SessionCleanupInterceptor implements HandlerInterceptor {

    private final SessionModelService sessionModelService;

    public SessionCleanupInterceptor(SessionModelService sessionModelService) {
        this.sessionModelService = sessionModelService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();

        if (uri.contains(".") || uri.startsWith("/css") || uri.startsWith("/js") || uri.startsWith("/webjars")) {
            return true;
        }

        List<String> safeZone = List.of(
                "/create", "/exercise-log", "/edit-exercise", "/set-exercise",
                "/add-exercise", "/remove-exercise", "/show", "/update-program-session",
                "/edit-program", "/close-show", "/close-set", "/close-log", "/create-full-workout"
        );

        boolean isInsideFlow = safeZone.stream().anyMatch(uri::startsWith);

        List<String> exitPoints = List.of("/home", "/my-workouts", "/profile", "/statistics", "/settings");
        boolean isExitPoint = exitPoints.stream().anyMatch(uri::startsWith);

        if (isExitPoint) {
            sessionModelService.clearSession(request.getSession());
        }

        return true;
    }
}
