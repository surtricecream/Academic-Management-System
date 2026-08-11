package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto;

import java.time.LocalDate;
import java.util.List;

public record StudentProfileDto(long personId, String personName, List<EnrolledUcDto> ucs, List<PendingEvaluationDto> pendingEvaluations) {
    public record EnrolledUcDto(long ucId, String ucName, Double average) {}

    public record PendingEvaluationDto(String type, long id, String title, LocalDate deadline, String ucName) {}
}