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
        using(groupConverter).map(source.getGroup(), destination.getGroupName());
    }

    private static final Converter<Group, String> groupConverter = context -> context.getSource().getName();
}
