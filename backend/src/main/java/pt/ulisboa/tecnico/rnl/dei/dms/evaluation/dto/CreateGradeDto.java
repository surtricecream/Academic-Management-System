package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto;

public record CreateGradeDto(Long testId, Long projectId, Long personId, Long groupId, Double score) {}
