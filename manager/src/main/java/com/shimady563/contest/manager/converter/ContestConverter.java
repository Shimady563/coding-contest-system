package com.shimady563.contest.manager.converter;

import com.shimady563.contest.manager.model.Contest;
import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.dto.ContestRequestDto;
import com.shimady563.contest.manager.model.dto.ContestResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public class ContestConverter {
    public static Contest request2Domain(ContestRequestDto contestRequestDto) {
        return ContestMapper.INSTANCE.request2Domain(contestRequestDto);
    }

    public static ContestResponseDto domain2Response(Contest contest) {
        return ContestMapper.INSTANCE.domain2Response(contest);
    }

    @Mapper
    public interface ContestMapper {
        ContestMapper INSTANCE = Mappers.getMapper(ContestMapper.class);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "group", ignore = true)
        @Mapping(target = "contestVersions", ignore = true)
        Contest request2Domain(ContestRequestDto contestRequestDto);

        @Mapping(source = "group", target = "groupId")
        ContestResponseDto domain2Response(Contest contest);

        default Long group2Long(Group group) {
            return group == null ? null : group.getId();
        }
    }
}
