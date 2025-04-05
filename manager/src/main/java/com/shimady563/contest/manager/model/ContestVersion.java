package com.shimady563.contest.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}

