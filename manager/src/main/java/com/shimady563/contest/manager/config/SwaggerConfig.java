package com.shimady563.contest.manager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "Manager Service",
                description = "Manager service of contest management system"
        )
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
}
