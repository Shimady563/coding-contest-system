package com.shimady563.contest.manager.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User response model")
public class UserResponseDto {
    @Schema(description = "User id", example = "10")
    private Long id;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Email", example = "student@example.com")
    private String email;

    @Schema(description = "Group name", example = "15/1")
    private String groupName;

    @Schema(description = "Group id", example = "1")
    private Long groupId;
}
