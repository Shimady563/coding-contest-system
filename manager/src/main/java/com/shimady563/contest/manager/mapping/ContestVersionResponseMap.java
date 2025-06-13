package com.shimady563.contest.manager.mapping;

import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.dto.ContestVersionResponseDto;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

import java.util.List;

public class ContestVersionResponseMap extends PropertyMap<ContestVersion, ContestVersionResponseDto> {
    @Override
    protected void configure() {
        map(source.getId(), destination.getId());
        map(source.getName(), destination.getName());
        using(TASKS_LIST_LONG_CONVERTER).map(source.getTasks(), destination.getTaskIds());
    }

    private static final Converter<List<Task>, List<Long>> TASKS_LIST_LONG_CONVERTER =
            context -> context.getSource().stream().map(Task::getId).toList();
}
