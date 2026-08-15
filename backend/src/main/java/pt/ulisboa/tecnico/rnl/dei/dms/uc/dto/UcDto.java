package pt.ulisboa.tecnico.rnl.dei.dms.uc.dto;

import java.util.List;

import pt.ulisboa.tecnico.rnl.dei.dms.course.domain.Course;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.domain.Uc;

public record UcDto(long id, String code, String name, Integer semester, Integer ects, long regenteId, String regenteName, 
                        List<Long> courseIds, List<String> courseNames) {
    public UcDto(Uc uc) {
        this(uc.getId(), uc.getCode(), uc.getName(), uc.getSemester(), uc.getEcts(), uc.getRegente().getId(), uc.getRegente().getName(), 
            uc.getCourses().stream().map(Course::getId).toList(),uc.getCourses().stream().map(Course::getName).toList());
    }
}
