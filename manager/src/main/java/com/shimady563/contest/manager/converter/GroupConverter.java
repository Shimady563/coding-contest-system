package com.shimady563.contest.manager.converter;

import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.dto.GroupResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

public class GroupConverter {
    public static GroupResponseDto domain2Response(Group group) {
        return GroupMapper.INSTANCE.domain2Response(group);
    }

    @Mapper
    public interface GroupMapper {
        GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

        GroupResponseDto domain2Response(Group group);
    }
}