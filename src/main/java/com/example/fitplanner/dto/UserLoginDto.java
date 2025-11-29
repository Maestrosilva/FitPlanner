package com.example.fitplanner.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserLoginDto {
    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    @Size(min = 4)
    private String password;
}
