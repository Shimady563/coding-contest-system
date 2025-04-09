package com.shimady563.contest.manager.mapping;

import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Solution;
import com.shimady563.contest.manager.model.Task;
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
        using(CONTEST_VERSION_STRING_CONVERTER).map(source.getTask().getContestVersion(),
                destination.getContestVersionName());
        using(CONTEST_STRING_CONVERTER).map(source.getTask().getContestVersion().getContest(),
                destination.getContestName());
    }

    private static final Converter<Task, String> TASK_STRING_CONVERTER =
            context -> context.getSource().getName();
    private static final Converter<ContestVersion, String> CONTEST_VERSION_STRING_CONVERTER =
            context -> context.getSource().getName();
    private static final Converter<Contest, String> CONTEST_STRING_CONVERTER = context ->
            context.getSource().getName();
}

