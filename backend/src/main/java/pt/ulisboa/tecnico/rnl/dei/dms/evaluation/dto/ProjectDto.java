package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto;

import java.time.LocalDate;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Project;

public record ProjectDto(long id, String title, LocalDate deadline, Double weight, Boolean isGroupProject, Integer maxGroupSize, long ucId, String ucName) {
    public ProjectDto(Project project) {
        this(project.getId(), project.getTitle(), project.getDeadline(), project.getWeight(), project.getIsGroupProject(),
                project.getMaxGroupSize(), project.getUc().getId(), project.getUc().getName());
    }
}
