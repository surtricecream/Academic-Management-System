package pt.ulisboa.tecnico.rnl.dei.dms.evaluation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.GradeDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.CreateGradeDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.service.GradeService;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@RestController
@RequestMapping("/api")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private PersonRepository personRepository;

    @PostMapping("/tests/{testId}/grade")
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR', 'TEACHING_ASSISTANT')")
    public ResponseEntity<GradeDto> gradeTest(@PathVariable long testId, @RequestBody CreateGradeDto dto, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
        
        GradeDto grade = gradeService.gradeTest(testId, dto, requester.getId());
        return ResponseEntity.ok(grade);
    }

    @PostMapping("/projects/{projectId}/grade/individual")
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR', 'TEACHING_ASSISTANT')")
    public ResponseEntity<GradeDto> gradeIndividualProject(@PathVariable long projectId, @RequestBody CreateGradeDto dto, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
        
        GradeDto grade = gradeService.gradeIndividualProject(projectId, dto, requester.getId());
        return ResponseEntity.ok(grade);
    }

    @PostMapping("/projects/{projectId}/grade/group")
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR', 'TEACHING_ASSISTANT')")
    public ResponseEntity<GradeDto> gradeGroupProject(@PathVariable long projectId, @RequestBody CreateGradeDto dto, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
        
        GradeDto grade = gradeService.gradeGroupProject(projectId, dto, requester.getId());
        return ResponseEntity.ok(grade);
    }
}