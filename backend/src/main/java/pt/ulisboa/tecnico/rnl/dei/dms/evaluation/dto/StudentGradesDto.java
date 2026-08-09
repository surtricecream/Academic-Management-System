package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto;

import java.util.List;

public record StudentGradesDto(long personId, String personName, long ucId, String ucName, List<GradeDto> grades, Double average) {}