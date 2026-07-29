package pt.ulisboa.tecnico.rnl.dei.dms.course.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.course.domain.Course;

// Data Transfer Object, to communicate with frontend
public record CourseDto(long id, String code, String name, Integer durationYears) {
    public CourseDto(Course course) {
        this(course.getId(), course.getCode(), course.getName(), course.getDurationYears());
    }
}