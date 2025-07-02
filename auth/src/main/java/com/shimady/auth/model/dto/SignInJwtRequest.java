package com.shimady.auth.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignInJwtRequest {
    @NotEmpty(message = "email cannot be empty")
    private String email;

    @NotEmpty(message = "password cannot be empty")
    private String password;
}
