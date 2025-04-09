package com.shimady563.contest.manager.mapping;

import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.dto.TaskResponseDto;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

public class TaskResponseDtoMap extends PropertyMap<Task, TaskResponseDto> {
    @Override
    protected void configure() {
        map(source.getId(), destination.getId());
        map(source.getName(), destination.getName());
        map(source.getDescription(), destination.getDescription());
        using(CONTEST_VERSION_LONG_CONVERTER).map(source.getContestVersion(), destination.getContestVersionId());
        map(source.getTestCases(), destination.getTestCases());
    }

    private static final Converter<ContestVersion, Long> CONTEST_VERSION_LONG_CONVERTER =
            context -> context.getSource().getId();
}

