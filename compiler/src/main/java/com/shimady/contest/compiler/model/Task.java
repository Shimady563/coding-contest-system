package com.shimady.contest.compiler.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task")
    private List<Solution> solutions = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "task")
    private Set<TestCase> testCases = new HashSet<>();

    public void addSolution(Solution solution) {
        solutions.add(solution);
        solution.setTask(this);
    }

    public void addTestCase(TestCase testCase) {
        testCases.add(testCase);
        testCase.setTask(this);
    }
}
