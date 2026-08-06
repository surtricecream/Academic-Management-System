package pt.ulisboa.tecnico.rnl.dei.dms.evaluation;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.CreateProjectGroupDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.dto.ProjectGroupDto;
import pt.ulisboa.tecnico.rnl.dei.dms.evaluation.service.ProjectGroupService;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;


@RestController
@RequestMapping("/projects/{projectId}/groups")
public class ProjectGroupController {

    @Autowired 
    private ProjectGroupService groupService;
    
    @Autowired
    private PersonRepository personRepository;

    @GetMapping
    public List<ProjectGroupDto> getGroups(@PathVariable long projectId) {
        return groupService.getGroups(projectId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR')")
    public ResponseEntity<ProjectGroupDto> createGroup(@PathVariable long projectId, @RequestBody CreateProjectGroupDto dto, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
    
        ProjectGroupDto created = groupService.createGroup(projectId, dto, requester.getId());
    
        URI location = URI.create(String.format("/projects/%d/groups/%d", projectId, created.id()));
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR')")
    public void deleteGroup(@PathVariable long projectId, @PathVariable long groupId, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
        groupService.deleteGroup(groupId, requester.getId());
    }

    @PostMapping("/auto-assign")
    @PreAuthorize("hasAnyRole('MAIN_TEACHER', 'ADMINISTRATOR')")
    public List<ProjectGroupDto> autoAssign(@PathVariable long projectId, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
    
        return groupService.autoAssignGroups(projectId, requester.getId());
    }
}
