package com.shimady.auth.model.dto;

import com.shimady.auth.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpJwtRequest {
    @NotEmpty(message = "first name cannot be blank")
    private String firstName;

    @NotEmpty(message = "last name cannot be blank")
    private String lastName;

    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            message = "wrong format of email")
    private String email;

    @Password(message = "password should be at least 8 characters long " +
            "and contain at least one lower case letter, " +
            "upper case letter, " +
            "digit, " +
            "symbol from @#$%^&+=")
    private String password;

    @NotNull(message = "group id cannot be null")
    private Long groupId;
}
