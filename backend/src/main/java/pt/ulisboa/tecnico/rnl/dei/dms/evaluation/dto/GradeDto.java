package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Grade;

public record GradeDto(long id, Long testId, String testTitle, Long projectId, String projectTitle, Long personId, 
                        String personName, Long groupId, Double score, long gradedById, String gradedByName) {
    public GradeDto(Grade grade) {
        this(grade.getId(),
                grade.getTest() != null ? grade.getTest().getId() : null,
                grade.getTest() != null ? grade.getTest().getTitle() : null,
                grade.getProject() != null ? grade.getProject().getId() : null,
                grade.getProject() != null ? grade.getProject().getTitle() : null,
                grade.getPerson() != null ? grade.getPerson().getId() : null,
                grade.getPerson() != null ? grade.getPerson().getName() : null,
                grade.getGroup() != null ? grade.getGroup().getId() : null,
                grade.getScore(),
                grade.getGradedBy().getId(), grade.getGradedBy().getName());
    }
}