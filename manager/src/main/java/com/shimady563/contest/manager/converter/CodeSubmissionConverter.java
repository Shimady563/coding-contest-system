package com.shimady563.contest.manager.converter;

import com.shimady563.contest.manager.model.dto.CodeSubmission;
import com.shimady563.contest.manager.model.dto.CodeSubmissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

public class CodeSubmissionConverter {
    public static CodeSubmission request2Transport(CodeSubmissionDto codeSubmissionDto) {
        return CodeSubmissionMapper.INSTANCE.request2Transport(codeSubmissionDto);
    }

    @Mapper
    public interface CodeSubmissionMapper {
        CodeSubmissionMapper INSTANCE = Mappers.getMapper(CodeSubmissionMapper.class);

        CodeSubmission request2Transport(CodeSubmissionDto codeSubmissionDto);
    }
}