package com.shimady563.contest.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "test_cases_count", nullable = false)
    private Short testCasesCount;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tasks")
    private Set<ContestVersion> contestVersions = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "task")
    private List<Solution> solutions = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "task", orphanRemoval = true)
    private Set<TestCase> testCases = new HashSet<>();

    public void addSolution(Solution solution) {
        solutions.add(solution);
        solution.setTask(this);
    }

    public void addTestCase(TestCase testCase) {
        if (testCases.add(testCase)) {
            testCase.setTask(this);
        }
    }

    public void removeTestCase(TestCase testCase) {
        testCases.remove(testCase);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task task)) return false;
        return Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && Objects.equals(testCasesCount, task.testCasesCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, testCasesCount);
    }
}
