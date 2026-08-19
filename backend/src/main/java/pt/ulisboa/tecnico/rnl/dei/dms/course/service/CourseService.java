package pt.ulisboa.tecnico.rnl.dei.dms.course.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.course.domain.Course;
import pt.ulisboa.tecnico.rnl.dei.dms.course.dto.CourseDto;
import pt.ulisboa.tecnico.rnl.dei.dms.course.repository.CourseRepository;

@Service
@Transactional
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;

    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("^[A-Z]{1,5}$");

    private Course catchCourseOrThrow(long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_COURSE, Long.toString(id)));
    }

    private void validateCourseData(CourseDto dto) {
        if (dto.code() == null || !COURSE_CODE_PATTERN.matcher(dto.code()).matches()) {
            throw new DEIException(ErrorMessage.INVALID_COURSE_DATA, "código inválido");
        }
        if (dto.name() == null || dto.name().isBlank() || dto.name().length() < 3 || dto.name().length() > 150) {
            throw new DEIException(ErrorMessage.INVALID_COURSE_DATA, "nome inválido");
        }
        if (dto.durationYears() == null || dto.durationYears() < 1 || dto.durationYears() > 3) {
            throw new DEIException(ErrorMessage.INVALID_COURSE_DATA, "duração deve ser entre 1 e 3 anos");
        }
    }

    private void validateCourseUniqueness(String code, Long excludeId) {
        courseRepository.findByCode(code)
                .filter(c -> excludeId == null || !c.getId().equals(excludeId))
                .ifPresent(c -> { throw new DEIException(ErrorMessage.COURSE_CODE_ALREADY_EXISTS, code); });
    }

    public List<CourseDto> getCourses() {
        return courseRepository.findAll().stream()
                .map(CourseDto::new)
                .toList();
    }

    public CourseDto createCourse(CourseDto courseDto) {
        validateCourseData(courseDto);
        validateCourseUniqueness(courseDto.code(), null);

        Course course = new Course(courseDto);
        course.setId(null);
        return new CourseDto(courseRepository.save(course));
    }

    public CourseDto getCourse(long id) {
        return new CourseDto(catchCourseOrThrow(id));
    }

    public CourseDto updateCourse(long id, CourseDto courseDto) {
        validateCourseData(courseDto);
        validateCourseUniqueness(courseDto.code(), id);

        Course course = catchCourseOrThrow(id);
            course.setCode(courseDto.code());
            course.setName(courseDto.name());
            course.setDurationYears(courseDto.durationYears());

        return new CourseDto(courseRepository.save(course));
    }

    public void deleteCourse(long id) {
        catchCourseOrThrow(id); // ensure it exists

        courseRepository.deleteById(id);
    }
}
