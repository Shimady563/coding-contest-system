package com.shimady.contest.compiler.config;

import org.springframework.context.annotation.Bean;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;

@Configuration
public class GenericConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
