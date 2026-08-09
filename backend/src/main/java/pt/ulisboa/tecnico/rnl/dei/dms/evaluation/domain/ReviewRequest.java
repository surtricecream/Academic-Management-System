package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;

@Data
@Entity
@Table(name = "review_requests")
public class ReviewRequest {

    public enum Status {
        PENDING,
        ASSISTANT_REVIEWED,
        APPROVED,
        REJECTED
    }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Person student;

    @Column(nullable = false, length = 1000)
    private String justification;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(length = 1000)
    private String assistantOpinion;

    @ManyToOne
    @JoinColumn(name = "assistant_id")
    private Person assistant;

    @Column(length = 1000)
    private String regenteDecision;

    @ManyToOne
    @JoinColumn(name = "decided_by_id")
    private Person decidedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime decidedAt;

    protected ReviewRequest() {}

    public ReviewRequest(Test test, Person student, String justification, LocalDateTime deadline) {
        this.test = test;
        this.student = student;
        this.justification = justification;
        this.deadline = deadline;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}