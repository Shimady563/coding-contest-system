package com.shimady.contest.compiler.converter;

import com.shimady.contest.compiler.model.Solution;
import com.shimady.contest.compiler.model.dto.SolutionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

public class SolutionConverter {
    public static SolutionResponse domain2Response(Solution solution) {
        return SolutionMapper.INSTANCE.domain2Response(solution);
    }

    @Mapper
    public interface SolutionMapper {
        SolutionMapper INSTANCE = Mappers.getMapper(SolutionMapper.class);

        SolutionResponse domain2Response(Solution solution);
    }
}