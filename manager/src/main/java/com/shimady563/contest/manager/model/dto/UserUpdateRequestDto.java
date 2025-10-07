package com.shimady563.contest.manager.model.dto;

import com.shimady563.contest.manager.validation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User update request")
public class UserUpdateRequestDto {
    @NotEmpty(message = "first name cannot be blank")
    @Size(min = 4, max = 64,
            message = "name length should be between 4 and 64 characters")
    @Schema(description = "First name", example = "John")
    private String firstName;

    @NotEmpty(message = "first name cannot be blank")
    @Size(min = 4, max = 64,
            message = "second name length should be between 4 and 64 characters")
    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            message = "wrong format of email")
    @Schema(description = "Email", example = "student@example.com")
    private String email;

    @Password(message = "password should be at least 8 characters long " +
            "and contain at least one lower case letter, " +
            "upper case letter, " +
            "digit, " +
            "symbol from @#$%^&+=")
    @Schema(description = "Email", example = "")
    private String password;

    @NotNull(message = "group id cannot be null")
    @Schema(description = "Target group id", example = "1")
    private Long groupId;
}
