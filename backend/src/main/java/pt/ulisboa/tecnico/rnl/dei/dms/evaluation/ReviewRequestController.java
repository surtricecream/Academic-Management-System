package pt.ulisboa.tecnico.rnl.dei.dms.evaluation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.ReviewRequestDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.service.ReviewRequestService;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.CreateReviewRequestDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.AssistantOpinionDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.RegenteDecisionDto;

@RestController
@RequestMapping("/review-requests")
public class ReviewRequestController {

    @Autowired
    private ReviewRequestService reviewRequestService;

    @Autowired
    private PersonRepository personRepository;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ReviewRequestDto create(@RequestBody CreateReviewRequestDto dto, Authentication auth) {
        Person requester = personRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, auth.getName()));

        return reviewRequestService.createRequest(dto, requester.getId());
    }

    @PostMapping("/{id}/assistant-opinion")
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'TEACHING_ASSISTANT', 'ADMINISTRATOR')")
    public ReviewRequestDto addOpinion(@PathVariable long id, @RequestBody AssistantOpinionDto dto, Authentication auth) {
        Person requester = personRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, auth.getName()));

        return reviewRequestService.addAssistantOpinion(id, dto, requester.getId());
    }

    @PostMapping("/{id}/decide")
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR')")
    public ReviewRequestDto decide(@PathVariable long id, @RequestBody RegenteDecisionDto dto, Authentication auth) {
        Person requester = personRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, auth.getName()));
        
        return reviewRequestService.decide(id, dto, requester.getId());
    }

    @GetMapping("/mine")
    public List<ReviewRequestDto> myRequests(Authentication auth) {
        Person requester = personRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, auth.getName()));

        return reviewRequestService.getStudentRequests(requester.getId(), requester.getId());
    }

    @GetMapping("/ucs/{ucId}")
    public List<ReviewRequestDto> ucRequests(@PathVariable long ucId, Authentication auth) {
        Person requester = personRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, auth.getName()));
        
        return reviewRequestService.getUcRequests(ucId, requester.getId());
    }
}
