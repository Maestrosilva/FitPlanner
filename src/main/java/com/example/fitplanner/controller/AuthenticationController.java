package com.example.fitplanner.controller;

import com.example.fitplanner.dto.UserLoginDto;
import com.example.fitplanner.dto.UserRegisterDto;
import com.example.fitplanner.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {
    @Autowired
    private UserService userService;
    @GetMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("registerDto", new UserRegisterDto());
        return "register-form";
    }

    @PostMapping("/register")
    public String postRegister(@Valid @ModelAttribute("registerDto") UserRegisterDto userRegisterDto,
                               BindingResult bindingResult){
        userService.validateUserRegister(userRegisterDto, bindingResult);
        if(bindingResult.hasErrors()) {
            return "register-form";
        }

        userService.save(userRegisterDto);
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String getLogin(Model model) {
        model.addAttribute("loginDto", new UserLoginDto());
        return "login-form";
    }

    @PostMapping("/login")
    public String postRegister(@Valid @ModelAttribute("loginDto") UserLoginDto userLoginDto,
                               BindingResult bindingResult){
        userService.validateUserLogin(userLoginDto, bindingResult);
        if(bindingResult.hasErrors()) {
            return "login-form";
        }
        return "redirect:/home";
    }
}