package com.shimady563.contest.manager.converter;

import com.shimady563.contest.manager.model.Task;
import com.shimady563.contest.manager.model.TestCase;
import com.shimady563.contest.manager.model.dto.TaskRequestDto;
import com.shimady563.contest.manager.model.dto.TaskResponseDto;
import com.shimady563.contest.manager.model.dto.TestCaseRequestDto;
import com.shimady563.contest.manager.model.dto.TestCaseResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;

public class TaskConverter {
    public static Task request2Domain(TaskRequestDto taskRequestDto) {
        return TaskMapper.INSTANCE.request2Domain(taskRequestDto);
    }

    public static TaskResponseDto domain2Response(Task contestVersion) {
        return TaskMapper.INSTANCE.domain2Response(contestVersion);
    }

    public static TestCase dto2TestCase(TestCaseRequestDto dto) {
        return TestCaseMapper.INSTANCE.request2Domain(dto);
    }

    @Mapper(uses = TestCaseMapper.class)
    public interface TaskMapper {
        TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "contestVersions", ignore = true)
        @Mapping(target = "solutions", ignore = true)
        @Mapping(target = "testCases", ignore = true)
        @Mapping(source = "testCases", target = "testCasesCount")
        Task request2Domain(TaskRequestDto taskRequestDto);

        TaskResponseDto domain2Response(Task contestVersion);

        default Short collection2Short(Collection<?> collection) {
            return (short) collection.size();
        }
    }

    @Mapper
    public interface TestCaseMapper {
        TestCaseMapper INSTANCE = Mappers.getMapper(TestCaseMapper.class);

        TestCaseResponseDto domain2Response(TestCase testCase);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "task", ignore = true)
        TestCase request2Domain(TestCaseRequestDto dto);
    }
}
