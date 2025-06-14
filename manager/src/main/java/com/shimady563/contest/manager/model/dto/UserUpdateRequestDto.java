package com.shimady563.contest.manager.model.dto;

import com.shimady563.contest.manager.validation.Password;
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
public class UserUpdateRequestDto {
    @NotEmpty(message = "first name cannot be blank")
    @Size(min = 4, max = 64,
            message = "name length should be between 4 and 64 characters")
    private String firstName;

    @NotEmpty(message = "first name cannot be blank")
    @Size(min = 4, max = 64,
            message = "second name length should be between 4 and 64 characters")
    private String lastName;

    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            message = "wrong format of email")
    private String email;

//    @Password(message = "password should be at least 8 characters long " +
//            "and contain at least one lower case letter, " +
//            "upper case letter, " +
//            "digit, " +
//            "symbol from @#$%^&+=")
//    private String password;

    @NotNull(message = "group id cannot be null")
    private Long groupId;
}
