package pt.ulisboa.tecnico.rnl.dei.dms.evaluation.service;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.Test;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.domain.ReviewRequest;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.ReviewRequestDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.CreateReviewRequestDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.AssistantOpinionDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.RegenteDecisionDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository.TestRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.repository.ReviewRequestRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.domain.Uc;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.repository.UcRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.domain.UcMembership;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.repository.UcMembershipRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@Service
@Transactional
public class ReviewRequestService {

    @Autowired
    private ReviewRequestRepository reviewRequestRepository;
    
    @Autowired
    private TestRepository testRepository;
    
    @Autowired
    private PersonRepository personRepository;
    
    @Autowired
    private UcMembershipRepository membershipRepository;

    @Autowired
    private UcRepository ucRepository;

    private ReviewRequest fetchOrThrow(long id) {
        return reviewRequestRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_REVIEW_REQUEST, Long.toString(id)));
    }

    private Test fetchTestOrThrow(long id) {
        return testRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_TEST, Long.toString(id)));
    }

    private Person fetchPersonOrThrow(long id) {
        return personRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(id)));
    }

    private Uc fetchUcOrThrow(long id) {
        return ucRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_UC, Long.toString(id)));
    }

    private void authorizeReviewAccess(Uc uc, long requesterId) { // regente, assistente or admin
        Person requester = fetchPersonOrThrow(requesterId);
        
        if (requester.getType() == Person.PersonType.ADMINISTRATOR) {
            return;
        }
        
        if (requesterId == uc.getRegente().getId()) {
            return;
        }
        
        boolean isAssistant = membershipRepository.findByUcIdAndPersonId(uc.getId(), requesterId)
            .map(m -> m.getRole() == UcMembership.MembershipRole.ASSISTENTE)
            .orElse(false);
        
        if (isAssistant) {
            return;
        }
        
        throw new DEIException(ErrorMessage.NOT_AUTHORIZED);
    }
    
    private void authorizeRegenteDecision(Uc uc, long requesterId) { // regente or admin
        Person requester = fetchPersonOrThrow(requesterId);
        
        if (requester.getType() == Person.PersonType.ADMINISTRATOR) {
            return;
        }
        
        if (requesterId == uc.getRegente().getId()) {
            return;
        }
        
        throw new DEIException(ErrorMessage.NOT_AUTHORIZED);
    }

    public ReviewRequestDto createRequest(CreateReviewRequestDto dto, long requesterId) {
        Test test = fetchTestOrThrow(dto.testId());
        Person student = fetchPersonOrThrow(requesterId);

        UcMembership membership = membershipRepository.findByUcIdAndPersonId(test.getUc().getId(), student.getId())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_MEMBERSHIP, Long.toString(student.getId())));
    
        if (membership.getRole() != UcMembership.MembershipRole.ALUNO) {
            throw new DEIException(ErrorMessage.MEMBER_NOT_STUDENT, Long.toString(student.getId()));
        }

        boolean requestExists = reviewRequestRepository.findByStudentId(student.getId()).stream()
            .anyMatch(r -> r.getTest().getId().equals(test.getId()));
        
        if (requestExists) {
            throw new DEIException(ErrorMessage.REVIEW_REQUEST_ALREADY_EXISTS);
        }
        
        if (dto.deadline() == null || dto.deadline().isBefore(java.time.LocalDateTime.now())) {
            throw new DEIException(ErrorMessage.INVALID_DEADLINE);
        }
        
        if (dto.justification() == null || dto.justification().trim().isEmpty()) {
            throw new DEIException(ErrorMessage.INVALID_JUSTIFICATION);
        }

        ReviewRequest request = new ReviewRequest(test, student, dto.justification(), dto.deadline());
        return new ReviewRequestDto(reviewRequestRepository.save(request));
    }

    public ReviewRequestDto addAssistantOpinion(long requestId, AssistantOpinionDto dto, long requesterId) {
        ReviewRequest request = fetchOrThrow(requestId);
        Person assistant = fetchPersonOrThrow(requesterId);
        Uc uc = request.getTest().getUc();
        
        authorizeReviewAccess(uc, requesterId);
        
        if (request.getStatus() != ReviewRequest.Status.PENDING) {
            throw new DEIException(ErrorMessage.REQUEST_ALREADY_REVIEWED);
        }
        
        request.setAssistantOpinion(dto.opinion());
        request.setAssistant(assistant);
        request.setStatus(ReviewRequest.Status.ASSISTANT_REVIEWED);
        
        return new ReviewRequestDto(reviewRequestRepository.save(request));
    }

    public ReviewRequestDto decide(long requestId, RegenteDecisionDto dto, long requesterId) {
        ReviewRequest request = fetchOrThrow(requestId);
        Person regente = fetchPersonOrThrow(requesterId);
        Uc uc = request.getTest().getUc();

        authorizeRegenteDecision(uc, requesterId);
        
        if (request.getStatus() == ReviewRequest.Status.APPROVED || request.getStatus() == ReviewRequest.Status.REJECTED) {
            throw new DEIException(ErrorMessage.REQUEST_ALREADY_DECIDED);
        }

        request.setRegenteDecision(dto.decisionNote());
        request.setDecidedBy(regente);
        request.setDecidedAt(LocalDateTime.now());
        request.setStatus(dto.approved() ? ReviewRequest.Status.APPROVED : ReviewRequest.Status.REJECTED);

        return new ReviewRequestDto(reviewRequestRepository.save(request));
    }

    public List<ReviewRequestDto> getStudentRequests(long studentId, long requesterId) {
        if (requesterId != studentId) {
            Person requester = fetchPersonOrThrow(requesterId);
            if (requester.getType() != Person.PersonType.ADMINISTRATOR) {
                throw new DEIException(ErrorMessage.NOT_AUTHORIZED);
            }
        }
        
        return reviewRequestRepository.findByStudentId(studentId).stream()
            .map(ReviewRequestDto::new)
            .toList();
    }

    public List<ReviewRequestDto> getUcRequests(long ucId, long requesterId) {
        Uc uc = fetchUcOrThrow(ucId);
        
        authorizeReviewAccess(uc, requesterId);

        List<ReviewRequest> ucRequests = reviewRequestRepository.findByTest_UcId(ucId);
        
        return ucRequests.stream()
            .map(ReviewRequestDto::new)
            .toList();
    }
}
