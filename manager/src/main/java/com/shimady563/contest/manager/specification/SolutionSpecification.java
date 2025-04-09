package com.shimady563.contest.manager.specification;

import com.shimady563.contest.manager.model.Solution;
import com.shimady563.contest.manager.model.Status;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class SolutionSpecification {
    public static Specification<Solution> hasStatus(Status status) {
        return ((root, query, builder) ->
                builder.equal(root.get("status"), status));
    }

    public static Specification<Solution> hasSubmittedAtBefore(LocalDateTime dateTime) {
        return ((root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("submittedAt"), dateTime));
    }

    public static Specification<Solution> hasSubmittedAtAfter(LocalDateTime dateTime) {
        return ((root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("submittedAt"), dateTime));
    }

    public static Specification<Solution> hasUserId(Long userId) {
        return ((root, query, builder) ->
                builder.equal(root.join("task")
                                .join("contestVersion")
                                .join("users")
                                .get("id"),
                        userId));
    }

    public static Specification<Solution> hasContestId(Long contestId) {
        return ((root, query, builder) ->
                builder.equal(root.join("task")
                                .join("contestVersion")
                                .join("contest")
                                .get("id"),
                        contestId));
    }
}
