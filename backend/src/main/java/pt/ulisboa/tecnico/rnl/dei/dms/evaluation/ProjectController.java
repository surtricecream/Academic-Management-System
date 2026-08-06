package pt.ulisboa.tecnico.rnl.dei.dms.evaluation;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.CreateProjectDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.ProjectDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.service.ProjectService;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@RestController
@RequestMapping("/ucs/{ucId}/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PersonRepository personRepository;

    @GetMapping
    public List<ProjectDto> getProjects(@PathVariable long ucId) {
        return projectService.getProjectsByUc(ucId);
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProject(@PathVariable long ucId, @PathVariable long projectId) {
        return projectService.getProject(projectId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<ProjectDto> createProject(@PathVariable long ucId, @RequestBody CreateProjectDto dto, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
        CreateProjectDto safeDto = new CreateProjectDto(dto.title(), dto.deadline(), dto.weight(),
                                        dto.isGroupProject(), dto.maxGroupSize(), ucId);
        ProjectDto created = projectService.createProject(ucId, safeDto, requester.getId());
        URI location = URI.create(String.format("/ucs/%d/projects/%d", ucId, created.id()));
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{projectId}")
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR')")
    public ProjectDto updateProject(@PathVariable long ucId, @PathVariable long projectId, @RequestBody CreateProjectDto dto, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
        CreateProjectDto safeDto = new CreateProjectDto(dto.title(), dto.deadline(), dto.weight(),
                                        dto.isGroupProject(), dto.maxGroupSize(), ucId);
        return projectService.updateProject(projectId, safeDto, requester.getId());
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR')")
    public void deleteProject(@PathVariable long ucId, @PathVariable long projectId, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
        projectService.deleteProject(projectId, requester.getId());
    }
}