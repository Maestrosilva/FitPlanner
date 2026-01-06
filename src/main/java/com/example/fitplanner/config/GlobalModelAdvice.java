package com.example.fitplanner.config;

import com.example.fitplanner.service.SessionModelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final SessionModelService sessionModelService;

    public GlobalModelAdvice(SessionModelService sessionModelService) {
        this.sessionModelService = sessionModelService;
    }

    @ModelAttribute
    public void handleModelPopulation(HttpSession session, Model model) {
        sessionModelService.populateModel(session, model);
    }
}
