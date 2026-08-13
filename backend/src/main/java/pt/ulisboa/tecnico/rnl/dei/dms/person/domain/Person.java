package pt.ulisboa.tecnico.rnl.dei.dms.person.domain;


import jakarta.persistence.*;

import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.CreatePersonDto;

// Domain class representing a person in the system
@Data
@Entity
@Table(name = "people")
public class Person {

	public enum PersonType {
		ADMINISTRATOR,
		MAIN_TEACHER,
		TEACHING_ASSISTANT,
		STUDENT
	}

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "ist_id", nullable = false, unique = true)
	private String istId;

	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
    private PersonType type;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	protected Person() {}

	public Person(String name, String istId, PersonType type, String email, String encodedPassword) {
		this.name = name;
		this.istId = istId;
		this.type = type;
		this.email = email;
		this.password = encodedPassword;
	}

	public Person(CreatePersonDto dto, String encodedPassword) {
		this(dto.name(), dto.istId(), dto.type(), dto.email(), encodedPassword);
	}
}
