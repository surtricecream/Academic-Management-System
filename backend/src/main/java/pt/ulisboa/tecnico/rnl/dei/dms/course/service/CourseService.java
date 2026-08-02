package pt.ulisboa.tecnico.rnl.dei.dms.course.service;

import java.util.List;

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

    private Course catchCourseOrThrow(long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_COURSE, Long.toString(id)));
    }

    public List<CourseDto> getCourses() {
        return courseRepository.findAll().stream()
                .map(CourseDto::new)
                .toList();
    }

    public CourseDto createCourse(CourseDto courseDto) {
        Course course = new Course(courseDto);
        course.setId(null);
        return new CourseDto(courseRepository.save(course));
    }

    public CourseDto getCourse(long id) {
        return new CourseDto(catchCourseOrThrow(id));
    }

    public CourseDto updateCourse(long id, CourseDto courseDto) {
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
