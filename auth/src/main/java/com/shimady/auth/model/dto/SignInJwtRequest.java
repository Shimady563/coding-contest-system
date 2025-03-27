package com.shimady.auth.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignInJwtRequest {
    private String email;
    private String password;
}
