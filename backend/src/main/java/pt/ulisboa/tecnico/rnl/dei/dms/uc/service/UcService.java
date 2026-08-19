package pt.ulisboa.tecnico.rnl.dei.dms.uc.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.course.repository.CourseRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.repository.UcRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.domain.Uc;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.dto.CreateUcDto;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.dto.UcDto;
import pt.ulisboa.tecnico.rnl.dei.dms.course.domain.Course;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;

@Service
@Transactional
public class UcService {

    @Autowired
    private UcRepository ucRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CourseRepository courseRepository;

    private static final Pattern UC_CODE_PATTERN = Pattern.compile("^[A-Z]{1,3}\\d?$");

    private Uc fetchUcOrThrow(long id) {
        return ucRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_UC, Long.toString(id)));
    }

    private void validateUcData(CreateUcDto dto) {
        if (dto.code() == null || !UC_CODE_PATTERN.matcher(dto.code()).matches()) {
            throw new DEIException(ErrorMessage.INVALID_UC_DATA, "código inválido");
        }
        if (dto.name() == null || dto.name().isBlank() || dto.name().length() < 3 || dto.name().length() > 150) {
            throw new DEIException(ErrorMessage.INVALID_UC_DATA, "nome inválido");
        }
        if (dto.semester() < 1 || dto.semester() > 2) {
            throw new DEIException(ErrorMessage.INVALID_UC_DATA, "semestre inválido");
        }
        if (dto.ects() != 3 && dto.ects() != 6) {
            throw new DEIException(ErrorMessage.INVALID_UC_DATA, "ECTS inválido");
        }
    }

    private void validateUcUniqueness(String code, Long excludeId) {
        ucRepository.findByCode(code)
                .filter(u -> excludeId == null || !u.getId().equals(excludeId))
                .ifPresent(u -> { throw new DEIException(ErrorMessage.UC_CODE_ALREADY_EXISTS, code); });
    }

    public List<UcDto> getUcs() {
        return ucRepository.findAll().stream()
            .map(UcDto::new)
            .toList();
    }

    public UcDto createUc(CreateUcDto ucDto) {
        validateUcData(ucDto);
        validateUcUniqueness(ucDto.code(), null);

        Person regente = personRepository.findById(ucDto.regenteId())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(ucDto.regenteId())));

        if (regente.getType() != Person.PersonType.MAIN_TEACHER) {
            throw new DEIException(ErrorMessage.INVALID_REGENTE);
        }

        List<Course> courses = courseRepository.findAllById(ucDto.courseIds());
        Uc uc = new Uc(ucDto.code(), ucDto.name(), ucDto.semester(), ucDto.ects(), regente, courses);
        uc.setId(null);
        return new UcDto(ucRepository.save(uc));
    }

    public UcDto getUc(long id) {
        return new UcDto(fetchUcOrThrow(id));
    }

    public UcDto updateUc(long id, CreateUcDto ucDto) {
        validateUcData(ucDto);
        validateUcUniqueness(ucDto.code(), id);

        Person regente = personRepository.findById(ucDto.regenteId())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(ucDto.regenteId())));

        if (regente.getType() != Person.PersonType.MAIN_TEACHER) {
            throw new DEIException(ErrorMessage.INVALID_REGENTE);
        }

        List<Course> courses = courseRepository.findAllById(ucDto.courseIds());
        Uc uc = fetchUcOrThrow(id);
            uc.setCode(ucDto.code());
            uc.setName(ucDto.name());
            uc.setSemester(ucDto.semester());
            uc.setEcts(ucDto.ects());
            uc.setRegente(regente);
            uc.setCourses(courses);

        return new UcDto(ucRepository.save(uc));
    }

    public void deleteUc(long id) {
        fetchUcOrThrow(id);
        ucRepository.deleteById(id);
    }
}
