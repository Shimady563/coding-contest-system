package com.shimady563.contest.manager.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User update request")
public class UserUpdateRequestDto {
    @NotBlank(message = "first name cannot be blank")
    @Schema(description = "First name", example = "John")
    private String firstName;

    @NotBlank(message = "last name cannot be blank")
    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            message = "wrong format of email")
    @Schema(description = "Email", example = "student@example.com")
    private String email;

    @Schema(description = "Password (empty for no changes)", example = "P@ssw0rd!")
    private String password;

    @Schema(description = "Target group id (null for teachers)", example = "1")
    private Long groupId;
}
