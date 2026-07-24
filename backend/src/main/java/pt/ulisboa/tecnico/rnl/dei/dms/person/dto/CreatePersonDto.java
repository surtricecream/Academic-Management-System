package pt.ulisboa.tecnico.rnl.dei.dms.person.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person.PersonType;

public record CreatePersonDto(String name, String istId, PersonType type, String email, String password) {
}
