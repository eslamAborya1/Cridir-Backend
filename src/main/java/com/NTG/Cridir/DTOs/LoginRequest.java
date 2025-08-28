package com.NTG.Cridir.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email(message = "Email format is invalid")
        String email,
        @NotBlank
        String password
) { }
