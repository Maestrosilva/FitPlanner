package com.example.fitplanner.util;

import com.example.fitplanner.entity.enums.Difficulty;
import com.example.fitplanner.entity.enums.Gender;
import com.example.fitplanner.entity.enums.Role;
import com.example.fitplanner.entity.model.User;
import com.example.fitplanner.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    final private SHA256Hasher hasher;
    public DataInitializer(UserRepository userRepository,
                           SHA256Hasher hasher) {
        this.userRepository = userRepository;
        this.hasher = hasher;
    }

    @PostConstruct
    public void init() {
        if (!userRepository.existsByRole(Role.ADMIN)) {
            User admin = new User(
                    "Admin",
                    "Admin",
                    "admin",
                    Role.ADMIN,
                    Gender.MALE,
                    30,
                    80.0,
                    Difficulty.BEGINNER,
                    hasher.hash("admin123"),
                    "admincho@gmail.com"
            );

            userRepository.save(admin);
        }
    }
}
