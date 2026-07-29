package pt.ulisboa.tecnico.rnl.dei.dms.course.domain;

import jakarta.persistence.*;
import lombok.Data;

// Domain class representing a course in the system
@Data
@Entity
@Table(name = "courses")
public class Course {

    @Id
	@GeneratedValue
	private Long id;

    @Column(name = "code", nullable = false)
	private String code;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "duration_Years", nullable = false)
	private Integer durationYears;

    protected Course() {
    }

    public Course(String code, String name, Integer durationYears) {
        this.code = code;
        this.name = name;
        this.durationYears = durationYears;
    }
    
}
