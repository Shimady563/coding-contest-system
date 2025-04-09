package com.shimady563.contest.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "contest_version")
public class ContestVersion {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "contest_version_id_seq"
    )
    @SequenceGenerator(
            name = "contest_version_id_seq",
            sequenceName = "contest_version_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "contestVersions")
    private Set<User> users = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "contestVersion",
            cascade = CascadeType.ALL
    )
    private Set<Task> tasks = new HashSet<>();

    public void addTask(Task task) {
        tasks.add(task);
        task.setContestVersion(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }
}

