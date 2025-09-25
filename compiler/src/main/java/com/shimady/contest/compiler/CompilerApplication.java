package com.shimady.contest.compiler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.shimady563.contest.compiler.config.props")
public class CompilerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompilerApplication.class, args);
    }

}
