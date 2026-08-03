package pt.ulisboa.tecnico.rnl.dei.dms.uc.service;

import java.util.List;

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

    private Uc fetchUcOrThrow(long id) {
        return ucRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_UC, Long.toString(id)));
    }

    public List<UcDto> getUcs() {
        return ucRepository.findAll().stream()
            .map(UcDto::new)
            .toList();
    }

    public UcDto createUc(CreateUcDto ucDto) {
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
