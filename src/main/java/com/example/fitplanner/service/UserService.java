package com.example.fitplanner.service;

import com.example.fitplanner.config.UnitValidator;
import com.example.fitplanner.dto.UserLoginDto;
import com.example.fitplanner.dto.UserRegisterDto;
import com.example.fitplanner.entity.model.User;
import com.example.fitplanner.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UnitValidator unitValidator;

    public void validateUserRegister(UserRegisterDto userRegisterDto, BindingResult bindingResult){
        if(!unitValidator.isValidUsername(userRegisterDto.getUsername())){
            bindingResult.rejectValue("username", "", "Invalid username format");
        }
        if(userRepository.getByUsername(userRegisterDto.getUsername()).isPresent()){
            bindingResult.rejectValue("username", "", "Username already exists");
        }
        if(userRepository.getByUsernameEmail(userRegisterDto.getEmail()).isPresent()){
            bindingResult.rejectValue("email", "", "Email already exists");
        }
        if(!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())){
            bindingResult.rejectValue("confirmPassword", "", "Passwords do not match");
        }
    }

    public void validateUserLogin(UserLoginDto userLoginDto, BindingResult bindingResult){
        if(unitValidator.isValidEmail(userLoginDto.getUsernameOrEmail())
                && userRepository.getByEmailAndPassword(userLoginDto.getUsernameOrEmail(), userLoginDto.getPassword()).isEmpty()){
            bindingResult.rejectValue("usernameOrEmail", "", "Email and Password do not match");
        }
        if(!unitValidator.isValidEmail(userLoginDto.getUsernameOrEmail())
        && userRepository.getByUsernameAndPassword(userLoginDto.getUsernameOrEmail(), userLoginDto.getPassword()).isEmpty()){
            bindingResult.rejectValue("usernameOrEmail", "", "Username and Password do not match");
        }
    }

    public void save(UserRegisterDto userRegisterDto) {
        User user = modelMapper.map(userRegisterDto, User.class);
        userRepository.save(user);
    }

    public void save(UserLoginDto userLoginDto) {
        User user = modelMapper.map(userLoginDto, User.class);
        userRepository.save(user);
    }
}