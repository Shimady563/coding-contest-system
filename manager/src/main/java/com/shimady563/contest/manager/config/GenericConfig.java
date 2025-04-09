package com.shimady563.contest.manager.config;

import com.shimady563.contest.manager.mapping.SolutionResponseDtoMap;
import com.shimady563.contest.manager.mapping.TaskResponseDtoMap;
import com.shimady563.contest.manager.mapping.UserResponseMap;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.addMappings(new UserResponseMap());
        mapper.addMappings(new SolutionResponseDtoMap());
        return new ModelMapper();
    }
}
