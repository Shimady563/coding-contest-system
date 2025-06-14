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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "contestVersions")
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "contest_version_task",
            joinColumns = @JoinColumn(name = "contest_version_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private Set<Task> tasks = new HashSet<>();

    public void addTask(Task task) {
        tasks.add(task);
        task.getContestVersions().add(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.getContestVersions().remove(this);
    }

    public void removeUsers() {
        users.forEach(u -> u.removeContestVersion(this));
        users.clear();
    }
}

