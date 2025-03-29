package com.shimady.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "student_group")
public class Group {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_group_id_seq"
    )
    @SequenceGenerator(
            name = "student_group_id_seq",
            sequenceName = "student_group_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group", cascade = CascadeType.REMOVE)
    private Set<User> users = new HashSet<>();

    public void addUser(User user) {
        users.add(user);
        user.setGroup(this);
    }
}

