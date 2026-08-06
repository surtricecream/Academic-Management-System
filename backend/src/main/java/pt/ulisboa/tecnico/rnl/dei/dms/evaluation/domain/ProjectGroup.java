package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;

@Data
@Entity
@Table(name = "project_groups")
public class ProjectGroup {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToMany
    @JoinTable(
        name = "project_group_members",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private List<Person> members = new ArrayList<>();

    protected ProjectGroup() {}

    public ProjectGroup(Project project, List<Person> members) {
        this.project = project;
        this.members = members;
    }
}