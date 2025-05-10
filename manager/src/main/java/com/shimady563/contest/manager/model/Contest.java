package com.shimady563.contest.manager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "contest")
public class Contest {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "contest_id_seq"
    )
    @SequenceGenerator(
            name = "contest_id_seq",
            sequenceName = "contest_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "contest",
            cascade = CascadeType.ALL
    )
    private Set<ContestVersion> contestVersions = new HashSet<>();

    public void addContestVersion(ContestVersion contestVersion) {
        contestVersions.add(contestVersion);
        contestVersion.setContest(this);
    }

    public void removeContestVersion(ContestVersion contestVersion) {
        contestVersions.remove(contestVersion);
    }
}
