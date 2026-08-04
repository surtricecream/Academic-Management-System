package pt.ulisboa.tecnico.rnl.dei.dms.ucmembership;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.dto.AddMembershipDto;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.dto.UcMembershipDto;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.service.UcMembershipService;

@RestController
@RequestMapping("/ucs/{ucId}/members")
public class UcMembershipController {

    @Autowired
    private UcMembershipService ucMembershipService;

    @Autowired
    private PersonRepository personRepository;

    @GetMapping
    public List<UcMembershipDto> getMembers(@PathVariable long ucId) {
        return ucMembershipService.getMembers(ucId);
    }

    @PostMapping
    public UcMembershipDto addMember(@PathVariable long ucId, @RequestBody AddMembershipDto dto, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
        return ucMembershipService.addMember(ucId, dto, requester.getId());
    }

    @DeleteMapping("/{personId}")
    public void removeMember(@PathVariable long ucId, @PathVariable long personId, Authentication authentication) {
        Person requester = personRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, authentication.getName()));
        ucMembershipService.removeMember(ucId, personId, requester.getId());
    }
}