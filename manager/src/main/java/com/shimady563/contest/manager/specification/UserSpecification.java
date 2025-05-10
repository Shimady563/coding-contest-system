package com.shimady563.contest.manager.specification;

import com.shimady563.contest.manager.model.Role;
import com.shimady563.contest.manager.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasFirstName(String firstName) {
        return ((root, query, builder) ->
                builder.equal(root.get("firstName"), firstName));
    }

    public static Specification<User> hasLastName(String lastName) {
        return ((root, query, builder) ->
                builder.equal(root.get("lastName"), lastName));
    }

    public static Specification<User> hasEmail(String email) {
        return ((root, query, builder) ->
                builder.equal(root.get("email"), email));
    }

    public static Specification<User> hasRole(Role role) {
        return ((root, query, builder) ->
                builder.equal(root.get("role"), role));
    }

    public static Specification<User> hasGroupName(String groupName) {
        return ((root, query, builder) ->
                builder.equal(root.join("group").get("name"), groupName));
    }
}
