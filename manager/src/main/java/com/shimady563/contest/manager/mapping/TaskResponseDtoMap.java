package com.shimady563.contest.manager.mapping;

import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.dto.TaskResponseDto;
import org.modelmapper.PropertyMap;

public class TaskResponseDtoMap extends PropertyMap<Task, TaskResponseDto> {
    @Override
    protected void configure() {
        map(source.getId(), destination.getId());
        map(source.getName(), destination.getName());
        map(source.getDescription(), destination.getDescription());
        map(source.getTestCases(), destination.getTestCases());
    }
}

