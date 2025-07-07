package com.shimady563.contest.manager.converter;

import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.dto.GroupRequestDto;
import com.shimady563.contest.manager.model.dto.GroupResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public class GroupConverter {
    public static GroupResponseDto domain2Response(Group group) {
        return GroupMapper.INSTANCE.domain2Response(group);
    }

    public static Group request2Domain(GroupRequestDto groupRequestDto) {
        return GroupMapper.INSTANCE.request2Domain(groupRequestDto);
    }

    @Mapper
    public interface GroupMapper {
        GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

        GroupResponseDto domain2Response(Group group);

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "users", ignore = true)
        @Mapping(target = "contests", ignore = true)
        Group request2Domain(GroupRequestDto groupRequestDto);
    }
}