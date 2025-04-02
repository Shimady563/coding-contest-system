package com.shimady.contest.compiler.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "solution")
public class Solution {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "solution_id_seq"
    )
    @SequenceGenerator(
            name = "solution_id_seq",
            sequenceName = "solution_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "tests_passed", nullable = false)
    private Short testsPassed;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}
