package pt.ulisboa.tecnico.rnl.dei.dms.person.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.CreatePersonDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.dto.PersonDto;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

// Service class for managing Person entities
@Service
@Transactional
public class PersonService {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Pattern IST_ID_PATTERN = Pattern.compile("^ist1\\d{6}$");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@tecnico\\.ulisboa\\.pt$");

	private Person fetchPersonOrThrow(long id) {
		return personRepository.findById(id)
				.orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(id)));
	}

	private void validateCreatePersonData(CreatePersonDto dto) {
    	if (dto.name() == null || dto.name().isBlank()) {
    	    throw new DEIException(ErrorMessage.INVALID_PERSON_DATA, "nome em falta");
    	}
    	if (dto.istId() == null || !IST_ID_PATTERN.matcher(dto.istId()).matches()) {
    	    throw new DEIException(ErrorMessage.INVALID_PERSON_DATA, "IST ID inválido");
    	}
    	if (dto.email() == null || !EMAIL_PATTERN.matcher(dto.email()).matches()) {
    	    throw new DEIException(ErrorMessage.INVALID_PERSON_DATA, "email inválido");
    	}
    	if (dto.password() == null || dto.password().isBlank()) {
    	    throw new DEIException(ErrorMessage.INVALID_PERSON_DATA, "password em falta");
    	}
    	if (dto.type() == null) {
    	    throw new DEIException(ErrorMessage.INVALID_PERSON_DATA, "tipo em falta");
    	}
		if (personRepository.findByIstId(dto.istId()).isPresent()) {
		    throw new DEIException(ErrorMessage.IST_ID_ALREADY_EXISTS, dto.istId());
		}
		if (personRepository.findByEmail(dto.email()).isPresent()) {
		    throw new DEIException(ErrorMessage.EMAIL_ALREADY_EXISTS, dto.email());
		}
	}

	private void validateUpdatePersonData(PersonDto dto) {
	    if (dto.name() == null || dto.name().isBlank()) {
	        throw new DEIException(ErrorMessage.INVALID_PERSON_DATA, "nome em falta");
	    }
	    if (dto.istId() == null || !IST_ID_PATTERN.matcher(dto.istId()).matches()) {
	        throw new DEIException(ErrorMessage.INVALID_PERSON_DATA, "IST ID inválido");
	    }
	    if (dto.email() == null || !EMAIL_PATTERN.matcher(dto.email()).matches()) {
	        throw new DEIException(ErrorMessage.INVALID_PERSON_DATA, "email inválido");
	    }
	    if (dto.type() == null || dto.type().isBlank()) {
	        throw new DEIException(ErrorMessage.INVALID_PERSON_DATA, "tipo em falta");
	    }
	}

	private void validateUpdatePersonUniqueness(PersonDto dto, long excludeId) {
		personRepository.findByIstId(dto.istId())
	            .filter(p -> !p.getId().equals(excludeId))
	            .ifPresent(p -> { throw new DEIException(ErrorMessage.IST_ID_ALREADY_EXISTS, dto.istId()); });
	    personRepository.findByEmail(dto.email())
	            .filter(p -> !p.getId().equals(excludeId))
	            .ifPresent(p -> { throw new DEIException(ErrorMessage.EMAIL_ALREADY_EXISTS, dto.email()); });
	}

	@Transactional
	public List<PersonDto> getPeople() {
		return personRepository.findAll().stream()
				.map(PersonDto::new)
				.toList();
	}

	@Transactional
	public PersonDto createPerson(CreatePersonDto personDto) {
		validateCreatePersonData(personDto);

		String encodedPassword = passwordEncoder.encode(personDto.password());
		Person person = new Person(personDto, encodedPassword);
		person.setId(null); 
		return new PersonDto(personRepository.save(person));
	}

	@Transactional
	public PersonDto getPerson(long id) {
		return new PersonDto(fetchPersonOrThrow(id));
	}

	@Transactional
	public PersonDto updatePerson(long id, PersonDto personDto) {
		validateUpdatePersonData(personDto);
		validateUpdatePersonUniqueness(personDto, id);

		Person person = fetchPersonOrThrow(id); 
		    person.setName(personDto.name());
    		person.setIstId(personDto.istId());
    		person.setEmail(personDto.email());
			person.setType(Person.PersonType.valueOf(personDto.type().toUpperCase()));
	
		return new PersonDto(personRepository.save(person));
	}

	@Transactional
	public void deletePerson(long id) {
		fetchPersonOrThrow(id); // ensure it exists

		personRepository.deleteById(id);
	}
}
