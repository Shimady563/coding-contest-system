package com.shimady563.contest.manager.mapping;

import com.shimady563.contest.manager.model.Group;
import com.shimady563.contest.manager.model.User;
import com.shimady563.contest.manager.model.dto.UserResponse;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

public class UserResponseMap extends PropertyMap<User, UserResponse> {
    @Override
    protected void configure() {
        map(source.getId(), destination.getId());
        map(source.getFirstName(), destination.getFirstName());
        map(source.getLastName(), destination.getLastName());
        map(source.getEmail(), destination.getEmail());
        using(GROUP_STRING_CONVERTER).map(source.getGroup(), destination.getGroupName());
        using(GROUP_LONG_CONVERTER).map(source.getGroup(), destination.getGroupId());
    }

    private static final Converter<Group, String> GROUP_STRING_CONVERTER = context -> context.getSource().getName();
    private static final Converter<Group, Long> GROUP_LONG_CONVERTER = context -> context.getSource().getId();
}
