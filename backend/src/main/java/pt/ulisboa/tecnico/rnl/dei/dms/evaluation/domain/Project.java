package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.domain.Uc;

@Data
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Boolean isGroupProject;

    private Integer maxGroupSize;

    @ManyToOne
    @JoinColumn(name = "uc_id", nullable = false)
    private Uc uc;

    protected Project() {}

    public Project(String title, LocalDate deadline, Double weight, Boolean isGroupProject, Integer maxGroupSize, Uc uc) {
        this.title = title;
        this.deadline = deadline;
        this.weight = weight;
        this.isGroupProject = isGroupProject;
        this.maxGroupSize = maxGroupSize;
        this.uc = uc;
    }
}