package pt.ulisboa.tecnico.rnl.dei.dms.evaluation;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.CreateTestDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.TestDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.service.TestService;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@RestController
@RequestMapping("/ucs/{ucId}/tests")
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private PersonRepository personRepository;

    @GetMapping
    public List<TestDto> getTests(@PathVariable long ucId) {
        return testService.getTestsByUc(ucId);
    }

    @GetMapping("/{testId}")
    public TestDto getTest(@PathVariable long ucId, @PathVariable long testId) {
        return testService.getTest(testId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<TestDto> createTest(@PathVariable long ucId, @RequestBody CreateTestDto dto, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
        CreateTestDto safeDto = new CreateTestDto(dto.title(), dto.date(), dto.weight(), ucId);
        TestDto created = testService.createTest(ucId, safeDto, requester.getId());
        URI location = URI.create(String.format("/ucs/%d/tests/%d", ucId, created.id()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{testId}")
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR')")
    public TestDto updateTest(@PathVariable long ucId, @PathVariable long testId, @RequestBody CreateTestDto dto, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
        CreateTestDto safeDto = new CreateTestDto(dto.title(), dto.date(), dto.weight(), ucId);
        return testService.updateTest(testId, safeDto, requester.getId());
    }

    @DeleteMapping("/{testId}")
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR')")
    public void deleteTest(@PathVariable long ucId, @PathVariable long testId, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
        testService.deleteTest(testId, requester.getId());
    }
}