package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto;

import java.time.LocalDateTime;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.ReviewRequest;
public record ReviewRequestDto(long id, long testId, String testTitle, long studentId, String studentName,
                                String justification, LocalDateTime deadline, String status,
                                String assistantOpinion, String assistantName,
                                String regenteDecision, String decidedByName,
                                LocalDateTime createdAt, LocalDateTime decidedAt) {
    public ReviewRequestDto(ReviewRequest r) {
        this(r.getId(), r.getTest().getId(), r.getTest().getTitle(), r.getStudent().getId(), r.getStudent().getName(),
                r.getJustification(), r.getDeadline(), r.getStatus().toString(),
                r.getAssistantOpinion(), r.getAssistant() != null ? r.getAssistant().getName() : null,
                r.getRegenteDecision(), r.getDecidedBy() != null ? r.getDecidedBy().getName() : null,
                r.getCreatedAt(), r.getDecidedAt());
    }
}
