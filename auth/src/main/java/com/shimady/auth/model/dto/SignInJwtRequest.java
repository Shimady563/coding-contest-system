package com.shimady.auth.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@Schema(description = "Login request payload to obtain JWT tokens")
public class SignInJwtRequest {
    @NotEmpty(message = "email cannot be empty")
    @Schema(description = "User email", example = "student@example.com")
    private String email;

    @NotEmpty(message = "password cannot be empty")
    @Schema(description = "User password", example = "P@ssw0rd!")
    private String password;
}
