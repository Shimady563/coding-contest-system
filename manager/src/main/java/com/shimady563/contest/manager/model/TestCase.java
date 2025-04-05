package com.shimady563.contest.manager.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "test_case")
public class TestCase {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "test_case_id_seq"
    )
    @SequenceGenerator(
            name = "test_case_id_seq",
            sequenceName = "test_case_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "input", nullable = false)
    private String input;

    @Column(name = "output", nullable = false)
    private String output;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}
