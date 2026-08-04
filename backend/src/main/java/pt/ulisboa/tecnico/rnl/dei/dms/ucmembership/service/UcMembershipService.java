package pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.domain.Uc;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.domain.UcMembership;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.repository.UcMembershipRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.uc.repository.UcRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.dto.UcMembershipDto;
import pt.ulisboa.tecnico.rnl.dei.dms.ucmembership.dto.AddMembershipDto;

@Service
@Transactional
public class UcMembershipService {

    @Autowired 
    private UcMembershipRepository membershipRepository;

    @Autowired 
    private UcRepository ucRepository;

    @Autowired 
    private PersonRepository personRepository;

    private Uc fetchUcOrThrow(long id) {
        return ucRepository.findById(id)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_UC, Long.toString(id)));
    }

    private Person fetchPersonOrThrow(long id) {
		return personRepository.findById(id)
				.orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(id)));
	}

    private UcMembership fetUcMembershipOrThrow(long ucId, long personId) {
        return membershipRepository.findByUcIdAndPersonId(ucId, personId)
            .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_MEMBERSHIP, Long.toString(personId)));
    }

    private void authorizeMembershipChange(Uc uc, long requesterId) {
        Person requester = fetchPersonOrThrow(requesterId);

        if (requester.getType() != Person.PersonType.ADMINISTRATOR && requesterId != uc.getRegente().getId()) {
            throw new DEIException(ErrorMessage.NOT_AUTHORIZED);
        }
    }

    public List<UcMembershipDto> getMembers(long ucId) {
        fetchUcOrThrow(ucId);

        return membershipRepository.findByUcId(ucId).stream().map(UcMembershipDto::new).toList();
    }

    public UcMembershipDto addMember(long ucId, AddMembershipDto dto, long requesterId) {
        Uc uc = fetchUcOrThrow(ucId);
        authorizeMembershipChange(uc, requesterId);
        
        Person person = fetchPersonOrThrow(dto.personId());

        membershipRepository.findByUcIdAndPersonId(ucId, person.getId())
            .ifPresent(existing -> {
                throw new DEIException(ErrorMessage.MEMBERSHIP_ALREADY_EXISTS, Long.toString(person.getId()));
            });
        
        UcMembership.MembershipRole role = UcMembership.MembershipRole.valueOf(dto.role().toUpperCase());
        UcMembership membership = new UcMembership(person, uc, role);
        return new UcMembershipDto(membershipRepository.save(membership));
    }

    public void removeMember(long ucId, long personId, long requesterId) {
        Uc uc = fetchUcOrThrow(ucId);
        authorizeMembershipChange(uc, requesterId);

        
        UcMembership membership = fetUcMembershipOrThrow(ucId, personId);
        membershipRepository.delete(membership);
    }
}
