package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain;

import jakarta.persistence.*;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;

@Data
@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;              // null if it is for a project

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;        // null if it is for a test

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;          // individual (test or solo project)

    @ManyToOne
    @JoinColumn(name = "group_id")
    private ProjectGroup group;     // group projects

    @Column(nullable = false)
    private Double score;

    @ManyToOne
    @JoinColumn(name = "graded_by_id", nullable = false)
    private Person gradedBy;

    protected Grade() {}

    public Grade(Test test, Project project, Person person, ProjectGroup group, Double score, Person gradedBy) {
        this.test = test;
        this.project = project;
        this.person = person;
        this.group = group;
        this.score = score;
        this.gradedBy = gradedBy;
    }
}