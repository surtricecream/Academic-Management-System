package pt.ulisboa.tecnico.rnl.dei.dms.uc.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.course.domain.Course;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;

// Domain class representing a uc in the system
@Data
@Entity
@Table(name = "ucs")
public class Uc {

    @Id
	@GeneratedValue
	private Long id;

    @Column(name = "code", nullable = false)
	private String code;

	@Column(name = "name", nullable = false)
	private String name;

    @Column(name = "semester", nullable = false)
	private Integer semester;

    @Column(name = "ects", nullable = false)
	private Integer ects;

    @ManyToOne
    @JoinColumn(name = "regente_id", nullable = false)
    private Person regente;

    @ManyToMany
    @JoinTable(
        name = "uc_courses",
        joinColumns = @JoinColumn(name = "uc_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )

    private List<Course> courses = new ArrayList<>();

    protected Uc() {}

    public Uc(String code, String name, Integer semester, Integer ects, Person regente, List<Course> courses) {
        this.code = code;
        this.name = name;
        this.semester = semester;
        this.ects = ects;
        this.regente = regente;
        this.courses = courses;
    }
}
