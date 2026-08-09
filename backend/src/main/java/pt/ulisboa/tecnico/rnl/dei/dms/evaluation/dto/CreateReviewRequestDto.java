package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto;

import java.time.LocalDateTime;

public record CreateReviewRequestDto(long testId, String justification, LocalDateTime deadline) {}