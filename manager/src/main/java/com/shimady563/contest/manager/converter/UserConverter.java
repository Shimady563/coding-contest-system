package com.shimady563.contest.manager.converter;

import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.model.dto.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public class UserConverter {
    public static UserResponseDto domain2Response(User user) {
        return UserMapper.INSTANCE.domain2Response(user);
    }

    @Mapper
    public interface UserMapper {
        UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

        @Mapping(source = "group", target = "groupId")
        @Mapping(source = "group", target = "groupName")
        UserResponseDto domain2Response(User user);

        default Long group2Id(Group group) {
            return group == null ? null : group.getId();
        }

        default String group2Name(Group group) {
            return group == null ? null : group.getName();
        }
    }
}