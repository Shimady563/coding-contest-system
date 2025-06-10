package com.shimady563.contest.manager.mapping;

import com.shimady563.contest.manager.model.*;
import com.shimady563.contest.manager.model.dto.SolutionResponseDto;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

public class SolutionResponseDtoMap extends PropertyMap<Solution, SolutionResponseDto> {
    @Override
    protected void configure() {
        map(source.getId(), destination.getId());
        map(source.getStatus(), destination.getStatus());
        map(source.getSubmittedAt(), destination.getSubmittedAt());
        map(source.getCode(), destination.getCode());
        using(TASK_STRING_CONVERTER).map(source.getTask(), destination.getTaskName());
        using(USER_STRING_CONVERTER).map(source.getUser(), destination.getUsername());
    }

    private static final Converter<Task, String> TASK_STRING_CONVERTER =
            context -> context.getSource().getName();
    private static final Converter<User, String> USER_STRING_CONVERTER =
            context -> context.getSource().getFirstName() + " " + context.getSource().getLastName();

}

