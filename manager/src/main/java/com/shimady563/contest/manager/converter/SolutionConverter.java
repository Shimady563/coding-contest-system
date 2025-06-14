package com.shimady563.contest.manager.converter;

import com.shimady563.contest.manager.model.Solution;
import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.model.dto.SolutionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public class SolutionConverter {
    public static SolutionResponseDto domain2Response(Solution solution) {
        return SolutionMapper.INSTANCE.domain2Response(solution);
    }

    @Mapper
    public interface SolutionMapper {
        SolutionMapper INSTANCE = Mappers.getMapper(SolutionMapper.class);

        @Mapping(source = "task", target = "taskName")
        @Mapping(source = "user", target = "username")
        SolutionResponseDto domain2Response(Solution solution);

        default String task2String(Task task) {
            return task == null ? null : task.getName();
        }

        default String user2String(User user) {
            return user == null ? null : user.getFirstName() + " " + user.getLastName();
        }
    }
}
