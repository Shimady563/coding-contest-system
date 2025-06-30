package com.shimady563.contest.manager.converter;

import com.shimady563.contest.manager.model.ContestVersion;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.dto.ContestVersionRequestDto;
import com.shimady563.contest.manager.model.dto.ContestVersionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public class ContestVersionConverter {
    public static ContestVersion request2Domain(ContestVersionRequestDto contestVersionRequestDto) {
        return ContestVersionMapper.INSTANCE.request2Domain(contestVersionRequestDto);
    }

    public static ContestVersionResponseDto domain2Response(ContestVersion contestVersion) {
        return ContestVersionMapper.INSTANCE.domain2Response(contestVersion);
    }

    @Mapper
    public interface ContestVersionMapper {
        ContestVersionMapper INSTANCE = Mappers.getMapper(ContestVersionMapper.class);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "contest", ignore = true)
        @Mapping(target = "users", ignore = true)
        @Mapping(target = "tasks", ignore = true)
        ContestVersion request2Domain(ContestVersionRequestDto contestVersionRequestDto);

        @Mapping(source = "tasks", target = "taskIds")
        ContestVersionResponseDto domain2Response(ContestVersion contestVersion);

        default Long task2Long(Task task) {
            return task == null ? null : task.getId();
        }
    }
}
